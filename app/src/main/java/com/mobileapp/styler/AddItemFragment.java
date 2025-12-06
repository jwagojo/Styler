package com.mobileapp.styler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddItemFragment extends Fragment {

    // IMPORTANT: Replace with your actual remove.bg API key
    private static final String REMOVE_BG_API_KEY = "oPnQrKwNRpK15gABFytvAcFa";
    private static final String TAG = "AddItemFragment";

    private FragmentAddItemBinding binding;
    private Uri selectedImageUri;
    private RemoveBgApiService apiService;

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
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void removeBackgroundAndSave() {
        if (selectedImageUri == null) {
            Toast.makeText(getContext(), "Please select an image first.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (REMOVE_BG_API_KEY.equals("YOUR_API_KEY")) {
            Toast.makeText(getContext(), "Please add your remove.bg API key.", Toast.LENGTH_LONG).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        File imageFile = new File(getRealPathFromURI(getContext(), selectedImageUri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
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
                    handleFailure("API error: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                handleFailure("Network error: " + t.getMessage());
            }
        });
    }

    private String saveImageToInternalStorage(ResponseBody body) {
        try {
            File outputDir = requireContext().getCacheDir();
            File outputFile = File.createTempFile(UUID.randomUUID().toString(), ".png", outputDir);
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(outputFile);
                byte[] fileReader = new byte[4096];
                long fileSizeDownloaded = 0;
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                }
                outputStream.flush();
                return outputFile.getAbsolutePath();
            } catch (IOException e) {
                return null;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return null;
        }
    }

    private void saveItemToDatabase(String imagePath) {
        String itemName = binding.editTextItemName.getText().toString().trim();
        String itemType = binding.editTextItemType.getText().toString().trim();

        Item item = new Item();
        item.name = itemName;
        item.type = itemType;
        item.imagePath = imagePath;

        AppDatabase.getDatabase(requireContext()).itemDao().insert(item);

        requireActivity().runOnUiThread(() -> {
            binding.progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Item saved!", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(AddItemFragment.this).popBackStack();
        });
    }

    private void handleFailure(String message) {
        requireActivity().runOnUiThread(() -> {
            binding.progressBar.setVisibility(View.GONE);
            Log.e(TAG, message);
            Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_LONG).show();
        });
    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
