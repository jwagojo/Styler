package com.mobileapp.styler;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.mobileapp.styler.db.AppDatabase;
import com.mobileapp.styler.db.Item;
import com.mobileapp.styler.db.ItemDao;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StylerViewModel extends AndroidViewModel {

    private final ItemDao itemDao;
    private final ExecutorService executorService;
    public final LiveData<List<Item>> tops;
    public final LiveData<List<Item>> bottoms;
    public final LiveData<List<Item>> shoes;

    private final MutableLiveData<Item> selectedTop = new MutableLiveData<>();
    private final MutableLiveData<Item> selectedBottom = new MutableLiveData<>();
    private final MutableLiveData<Item> selectedShoe = new MutableLiveData<>();

    public StylerViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        itemDao = db.itemDao();
        executorService = Executors.newSingleThreadExecutor();
        tops = itemDao.getItemsByType("top");
        bottoms = itemDao.getItemsByType("bottom");
        shoes = itemDao.getItemsByType("shoe");
    }

    public void deleteItem(Item item) {
        executorService.execute(() -> itemDao.delete(item));
    }

    public void setSelectedTop(Item top) {
        selectedTop.setValue(top);
    }

    public LiveData<Item> getSelectedTop() {
        return selectedTop;
    }

    public void setSelectedBottom(Item bottom) {
        selectedBottom.setValue(bottom);
    }

    public LiveData<Item> getSelectedBottom() {
        return selectedBottom;
    }

    public void setSelectedShoe(Item shoe) {
        selectedShoe.setValue(shoe);
    }

    public LiveData<Item> getSelectedShoe() {
        return selectedShoe;
    }
}
