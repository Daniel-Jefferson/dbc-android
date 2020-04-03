package com.app.budbank.utils.cacheUtils;

import android.content.Context;

import com.app.budbank.models.DispensaryModel;
import com.app.budbank.models.VoucherModel;
import com.app.budbank.utils.AppConstants;
import com.app.budbank.utils.StorageUtillity;
import com.app.budbank.web.APIController;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Iterator;

public class MainStorageUtils {
    private static MainStorageUtils mainStorageUtils;
    private ArrayList<DispensaryModel> dispensariesList;
    private ArrayList<DispensaryModel> availableDispensariesList;
    private ArrayList<DispensaryModel> completedDispensariesList;
    private ArrayList<DispensaryModel> featuredDispensariesList;
    private ArrayList<VoucherModel> availableVouchersList;
    private ArrayList<VoucherModel> redeemedVouchersList;

    private MainStorageUtils() {
        dispensariesList = new ArrayList<>();
        completedDispensariesList = new ArrayList<>();
        availableVouchersList = new ArrayList<>();
        redeemedVouchersList = new ArrayList<>();
    }

    public static MainStorageUtils getInstance() {
        if (mainStorageUtils == null)
            mainStorageUtils = new MainStorageUtils();
        return mainStorageUtils;
    }

    public void setLists(ArrayList<DispensaryModel> dispensariesList, ArrayList<DispensaryModel> completedDispensariesList,ArrayList<DispensaryModel> featuredDispensariesList, ArrayList<VoucherModel> availableVouchersList, ArrayList<VoucherModel> redeemedVouchersList) {
        this.dispensariesList = dispensariesList;
        this.completedDispensariesList = completedDispensariesList;
        this.featuredDispensariesList = featuredDispensariesList;
        this.availableVouchersList = availableVouchersList;
        this.redeemedVouchersList = redeemedVouchersList;
    }

    public ArrayList<DispensaryModel> getDispensariesList() {
        return dispensariesList;
    }

    public ArrayList<DispensaryModel> getAvailableDispensariesList() {
        return availableDispensariesList;
    }

    public void setAvailableDispensariesList(ArrayList<DispensaryModel> availableDispensariesList) {
        this.availableDispensariesList = availableDispensariesList;
    }

    public ArrayList<DispensaryModel> getCompletedDispensariesList() {
        return completedDispensariesList;
    }

    public ArrayList<DispensaryModel> getFeaturedDispensariesList() {
        return featuredDispensariesList;
    }

    public ArrayList<VoucherModel> getAvailableVouchersList() {
        if (availableVouchersList == null)
            return null;
        ArrayList<VoucherModel> copyList = new ArrayList<>();
        for (VoucherModel item : availableVouchersList) {
            copyList.add(new VoucherModel(item));
        }
        return copyList;
    }

    public ArrayList<VoucherModel> getRedeemedVouchersList() {
        if (redeemedVouchersList == null)
            return null;
        ArrayList<VoucherModel> copyList = new ArrayList<>();
        for (VoucherModel item : redeemedVouchersList) {
            copyList.add(new VoucherModel(item));
        }
        return copyList;
    }

    public void addCompletedVoucher(VoucherModel voucherModel) {
        if (voucherModel == null)
            return;
        if (availableVouchersList == null)
            availableVouchersList = new ArrayList<>();
        availableVouchersList.add(voucherModel);
    }

    private void removeAvailableVoucher(VoucherModel voucherModel) {
        if (availableVouchersList == null || voucherModel == null)
            return;
        Iterator<VoucherModel> iterator = availableVouchersList.iterator();
        while (iterator.hasNext()) {
            VoucherModel availableVoucher = iterator.next();
            if (availableVoucher.getDispensaryId() == voucherModel.getDispensaryId()) {
                iterator.remove();
                break;
            }
        }
    }

    private void removeDispensary(DispensaryModel dispensaryModel) {
        if (dispensariesList == null || dispensaryModel == null)
            return;
        Iterator<DispensaryModel> iterator = availableDispensariesList.iterator();
        while (iterator.hasNext()) {
            DispensaryModel availableVoucher = iterator.next();
            if (availableVoucher.getId() == dispensaryModel.getId()) {
                iterator.remove();
                break;
            }
        }
    }

    public void addRedeemedVoucher(VoucherModel voucherModel) {
        if (voucherModel == null)
            return;
        if (redeemedVouchersList == null)
            redeemedVouchersList = new ArrayList<>();
        removeAvailableVoucher(voucherModel);
        redeemedVouchersList.add(0, voucherModel);
    }

    public void addCompletedDispensary(DispensaryModel dispensaryModel) {
        if (dispensaryModel == null)
            return;
        if (completedDispensariesList == null)
            completedDispensariesList = new ArrayList<>();
        removeDispensary(dispensaryModel);
        completedDispensariesList.add(0, dispensaryModel);
        if(completedDispensariesList.size() > AppConstants.PAGE_SIZE)
            removeLastCompletedDispensary();
    }

    public void removeLastCompletedDispensary() {
        completedDispensariesList.remove(completedDispensariesList.size()-1);
    }

    public boolean isDispensaryAvailable(int id) {
        if (completedDispensariesList == null || completedDispensariesList.size() == 0)
            return true;
        for (DispensaryModel dispensaryModel: completedDispensariesList) {
            if (id == dispensaryModel.getId())
                return false;
        }

        return true;
    }

    public void updateDispensary(DispensaryModel dispensaryModel) {
        int index = 0;
        for (DispensaryModel dModel: dispensariesList) {
            if (dModel.getId() == dispensaryModel.getId()) {
                dispensariesList.set(index, dispensaryModel);
                break;
            }
            index++;
        }

        index = 0;
        for (DispensaryModel dModel: completedDispensariesList) {
            if (dModel.getId() == dispensaryModel.getId()) {
                completedDispensariesList.set(index, dispensaryModel);
                break;
            }
            index++;
        }
    }
}
