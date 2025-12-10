package com.mobileapp.styler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.mobileapp.styler.databinding.FragmentAddItemBinding;
import com.mobileapp.styler.db.AppDatabase;
import com.mobileapp.styler.db.Item;
import com.mobileapp.styler.network.RemoveBgApiService;
import com.mobileapp.styler.network.RetrofitClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddItemFragment extends Fragment {

    private static final String REMOVE_BG_API_KEY = "oPnQrKwNRpK15gABFytvAcFa";
    private static final String TAG = "AddItemFragment";

    private FragmentAddItemBinding binding;
    private Uri selectedImageUri;
    private RemoveBgApiService apiService;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    binding.imagePreview.setImageURI(selectedImageUri);
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddItemBinding.inflate(inflater, container, false);
        apiService = RetrofitClient.getClient().create(RemoveBgApiService.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSelectImage.setOnClickListener(v -> openGallery());
        binding.buttonSave.setOnClickListener(v -> removeBackgroundAndSave());
        binding.fabBack.setOnClickListener(v -> {
            NavHostFragment.findNavController(AddItemFragment.this).popBackStack();
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void removeBackgroundAndSave() {
        String itemName = binding.editTextItemName.getText().toString().trim();
        String itemType = binding.editTextItemType.getText().toString().trim().toLowerCase();

        // Reset previous errors
        binding.editTextItemName.setError(null);
        binding.editTextItemType.setError(null);

        // --- Validation Logic ---
        if (itemName.isEmpty()) {
            binding.editTextItemName.setError("Item name is required");
            return;
        }

        if (itemType.isEmpty()) {
            binding.editTextItemType.setError("Item type is required");
            return;
        }

        boolean isValidType = itemType.equals("top") || itemType.equals("bottom") || itemType.equals("shoe");
        if (!isValidType) {
            binding.editTextItemType.setError("Type must be 'top', 'bottom', or 'shoe'");
            return;
        }
        // --- End Validation ---

        if (selectedImageUri == null) {
            handleFailure("Please select an image first.");
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        File imageFile = createFileFromUri(getContext(), selectedImageUri);
        if (imageFile == null) {
            handleFailure("Failed to create temp file for upload.");
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse(getContext().getContentResolver().getType(selectedImageUri)), imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image_file", imageFile.getName(), requestFile);

        apiService.removeBackground(REMOVE_BG_API_KEY, body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String processedImagePath = saveImageToInternalStorage(response.body());
                    if (processedImagePath != null) {
                        saveItemToDatabase(processedImagePath);
                    } else {
                        handleFailure("Failed to save processed image.");
                    }
                } else {
                    handleFailure("API error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                handleFailure("Network error: " + t.getMessage());
            }
        });
    }

    private String saveImageToInternalStorage(ResponseBody body) {
        File outputFile;
        try {
            outputFile = File.createTempFile("processed_", ".png", requireContext().getCacheDir());
            try (InputStream inputStream = body.byteStream();
                 OutputStream outputStream = new FileOutputStream(outputFile)) {
                byte[] fileReader = new byte[4096];
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                }
                outputStream.flush();
                return outputFile.getAbsolutePath();
            } catch (IOException e) {
                handleFailure("Failed to save processed image stream.");
                return null;
            }
        } catch (IOException e) {
            handleFailure("Failed to create temp file for saving.");
            return null;
        }
    }

    private void saveItemToDatabase(String imagePath) {
        String itemName = binding.editTextItemName.getText().toString().trim();
        String itemType = binding.editTextItemType.getText().toString().trim().toLowerCase(); // Always save as lowercase

        Item item = new Item();
        item.name = itemName;
        item.type = itemType;
        item.imagePath = imagePath;

        executorService.execute(() -> {
            AppDatabase.getDatabase(requireContext()).itemDao().insert(item);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Item saved!", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(AddItemFragment.this).popBackStack();
                });
            }
        });
    }

    private void handleFailure(String message) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                binding.progressBar.setVisibility(View.GONE);
                Log.e(TAG, message);
                Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_LONG).show();
            });
        }
    }

    private File createFileFromUri(Context context, Uri uri) {
        File outputFile;
        try {
            String fileExtension = ".jpg";
            if (uri != null && context.getContentResolver() != null) {
                String mimeType = context.getContentResolver().getType(uri);
                if (mimeType != null) {
                    if (mimeType.contains("png")) fileExtension = ".png";
                    else if (mimeType.contains("gif")) fileExtension = ".gif";
                }
            }
            outputFile = File.createTempFile("upload_", fileExtension, context.getCacheDir());

            try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
                 OutputStream outputStream = new FileOutputStream(outputFile)) {
                if (inputStream == null) return null;
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            }
            return outputFile;
        } catch (IOException e) {
            Log.e(TAG, "Error creating file from URI", e);
            return null;
        }
    }
}
