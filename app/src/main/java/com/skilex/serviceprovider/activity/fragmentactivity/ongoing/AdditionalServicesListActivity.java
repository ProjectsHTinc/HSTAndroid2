package com.skilex.serviceprovider.activity.fragmentactivity.ongoing;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.skilex.serviceprovider.R;
import com.skilex.serviceprovider.adapter.AdditionalServiceListAdapter;
import com.skilex.serviceprovider.bean.support.AdditionalService;
import com.skilex.serviceprovider.bean.support.AdditionalServiceList;
import com.skilex.serviceprovider.bean.support.OngoingService;
import com.skilex.serviceprovider.helper.AlertDialogHelper;
import com.skilex.serviceprovider.helper.ProgressDialogHelper;
import com.skilex.serviceprovider.interfaces.DialogClickListener;
import com.skilex.serviceprovider.languagesupport.BaseActivity;
import com.skilex.serviceprovider.servicehelpers.ServiceHelper;
import com.skilex.serviceprovider.serviceinterfaces.IServiceListener;
import com.skilex.serviceprovider.utils.CommonUtils;
import com.skilex.serviceprovider.utils.PreferenceStorage;
import com.skilex.serviceprovider.utils.SkilExConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;

public class AdditionalServicesListActivity extends BaseActivity implements IServiceListener, DialogClickListener {

    private static final String TAG = AdditionalServicesListActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    ArrayList<AdditionalService> serviceArrayList = new ArrayList<>();
    int totalCount = 0, checkrun = 0;
    protected boolean isLoadingForFirstTime = true;
    AdditionalServiceListAdapter serviceListAdapter;
    ListView loadMoreListView;
    String ser = "";
    OngoingService ongoingService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_services_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        ser = getIntent().getStringExtra("serviceObj");
//        ongoingService = (OngoingService) getIntent().getSerializableExtra("serviceObj");

        loadMoreListView = findViewById(R.id.listSumService);
        callGetSubCategoryService();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void callGetSubCategoryService() {
        if (CommonUtils.isNetworkAvailable(this)) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            loadCart();
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.error_no_net));
        }
    }

    private void loadCart() {
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = ser;
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, PreferenceStorage.getUserMasterId(getApplicationContext()));
            jsonObject.put(SkilExConstants.SERVICE_ORDER_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.VIEW_ADDITIONAL_SERVICES;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    private boolean validateSignInResponse(JSONObject response) {
        boolean signInSuccess = false;
        if ((response != null)) {
            try {
                String status = response.getString("status");
                String msg = response.getString(SkilExConstants.PARAM_MESSAGE);
                d(TAG, "status val" + status + "msg" + msg);

                if ((status != null)) {
                    if (((status.equalsIgnoreCase("activationError")) || (status.equalsIgnoreCase("alreadyRegistered")) ||
                            (status.equalsIgnoreCase("notRegistered")) || (status.equalsIgnoreCase("error")))) {
                        signInSuccess = false;
                        d(TAG, "Show error dialog");
                        AlertDialogHelper.showSimpleAlertDialog(this, msg);

                    } else {
                        signInSuccess = true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return signInSuccess;
    }

    @Override
    public void onResponse(JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        if (validateSignInResponse(response)) {
            try {
                JSONArray getData = response.getJSONArray("service_list");
//                    loadMembersList(getData.length());
                Gson gson = new Gson();
                AdditionalServiceList ongoingServiceList = gson.fromJson(response.toString(), AdditionalServiceList.class);
                if (ongoingServiceList.getserviceArrayList() != null && ongoingServiceList.getserviceArrayList().size() > 0) {
                    totalCount = ongoingServiceList.getCount();
//                    this.ongoingServiceArrayList.addAll(ongoingServiceList.getserviceArrayList());
                    isLoadingForFirstTime = false;
                    updateListAdapter(ongoingServiceList.getserviceArrayList());
                } else {
                    if (serviceArrayList != null) {
                        serviceArrayList.clear();
                        updateListAdapter(ongoingServiceList.getserviceArrayList());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected void updateListAdapter(ArrayList<AdditionalService> serviceArrayLists) {
        serviceArrayList.clear();
        serviceArrayList.addAll(serviceArrayLists);
        if (serviceListAdapter == null) {
            serviceListAdapter = new AdditionalServiceListAdapter(this, serviceArrayList);
            loadMoreListView.setAdapter(serviceListAdapter);
        } else {
            serviceListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }
}
