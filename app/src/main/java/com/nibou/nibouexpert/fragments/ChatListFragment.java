package com.nibou.nibouexpert.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.nibou.nibouexpert.Dialogs.AppDialogs;
import com.nibou.nibouexpert.R;
import com.nibou.nibouexpert.activitys.ChatActivity;
import com.nibou.nibouexpert.adapter.ChatListAdapter;
import com.nibou.nibouexpert.api.ApiClient;
import com.nibou.nibouexpert.api.ApiEndPoint;
import com.nibou.nibouexpert.api.ApiHandler;
import com.nibou.nibouexpert.databinding.FragmentChatListBinding;
import com.nibou.nibouexpert.models.ActiveChatSessionModel;
import com.nibou.nibouexpert.models.AppSessionModel;
import com.nibou.nibouexpert.utils.*;

import java.util.ArrayList;


public class ChatListFragment extends Fragment {

    private FragmentChatListBinding binding;
    private Context context;
    private ChatListAdapter chatListAdapter;
    private ActiveChatSessionModel activeChatSessionModel;

    private boolean isRefreshing = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat_list, container, false);
        context = getActivity();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            getActivity().registerReceiver(broadcastRefreshReceiver, new IntentFilter(AppConstant.NEW_MESSAGE_RECIEVER));
        } catch (Exception e) {
            e.printStackTrace();
        }
        init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(broadcastRefreshReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {

        binding.crossImg.setOnClickListener(v -> {
            binding.search.getText().clear();
            binding.searchImg.setVisibility(View.VISIBLE);
            binding.crossImg.setVisibility(View.GONE);
        });

        binding.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 2) {
                    if (AppUtil.isInternetAvailable(context)) {
                        getSearchNetworkCall(s.toString());
                    } else {
                        AppUtil.showToast(context, getString(R.string.internet_error));
                    }
                }

                binding.searchImg.setVisibility(View.GONE);
                binding.crossImg.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {

                    if (AppUtil.isInternetAvailable(context)) {
                        getChatSessionNetworkCall();
                    } else {
                        AppUtil.showToast(context, getString(R.string.internet_error));
                    }

                    binding.searchImg.setVisibility(View.VISIBLE);
                    binding.crossImg.setVisibility(View.GONE);
                    AppUtil.hideKeyBoard(getActivity());
                }
            }
        });

        if (AppUtil.isInternetAvailable(context)) {
            getChatSessionNetworkCall();
        } else {
            AppUtil.showToast(context, getString(R.string.internet_error));
        }
    }

    private void showSessionList() {
        if (chatListAdapter == null) {
            binding.chatList.setLayoutManager(new LinearLayoutManager(getActivity()));
            chatListAdapter = new ChatListAdapter(getActivity(), activeChatSessionModel, position -> {
                LocalPrefences.getInstance().putBoolean(context, AppConstant.DELAY_ALERT + activeChatSessionModel.getData().get(position).getId(), false);
                LocalPrefences.getInstance().putInt(context, activeChatSessionModel.getData().get(position).getId(), 0);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(AppConstant.CUSTOMER_DETAILS_MODEL, activeChatSessionModel.getData().get(position));
                intent.putExtra(ChatConstants.ROOM_ID, activeChatSessionModel.getData().get(position).getId());
                intent.putExtra("is_private", activeChatSessionModel.getData().get(position).getAttributes().isIs_private());
                startActivityForResult(intent, 100);
            });
            binding.chatList.setAdapter(chatListAdapter);
        } else {
            chatListAdapter.updateList(activeChatSessionModel);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (binding != null) {
            if (chatListAdapter != null)
                chatListAdapter.updateList(activeChatSessionModel);
            if (AppUtil.isInternetAvailable(context)) {
                getChatSessionNetworkCall();
            } else {
                AppUtil.showToast(context, getString(R.string.internet_error));
            }
        }
    }

    BroadcastReceiver broadcastRefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (binding != null) {
                    if (intent.hasExtra(AppConstant.APP_SESSION_MODEL)) {
                        updateLastMesssageTimeStamp((AppSessionModel) intent.getSerializableExtra(AppConstant.APP_SESSION_MODEL));
                    } else {
                        if (AppUtil.isInternetAvailable(context)) {
                            getChatSessionNetworkCall();
                        } else {
                            AppUtil.showToast(context, getString(R.string.internet_error));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void updateLastMesssageTimeStamp(AppSessionModel appSessionModel) {
        try {
            if (activeChatSessionModel != null && activeChatSessionModel.getData() != null) {
                for (int i = 0; i < activeChatSessionModel.getData().size(); i++) {
                    if (activeChatSessionModel.getData().get(i).getId().equals(appSessionModel.getRoom().getData().getId())) {
                        if (activeChatSessionModel.getData().get(i).getAttributes().getLast_message() != null) {
                            activeChatSessionModel.getData().get(i).getAttributes().getLast_message().getData().getAttributes().setCreated_at(appSessionModel.getMessage().getData().getAttributes().getCreated_at());
                            activeChatSessionModel.getData().get(i).getAttributes().getLast_message().getData().getAttributes().setText(appSessionModel.getMessage().getData().getAttributes().getText());
                            activeChatSessionModel.getData().get(i).getAttributes().getLast_message().getData().getAttributes().setImages(appSessionModel.getMessage().getData().getAttributes().getImages());
                        } else {
                            activeChatSessionModel.getData().get(i).getAttributes().setLast_message(appSessionModel.getMessage());
                        }
                    }
                }
                activeChatSessionModel.sort();
                new Handler().postDelayed(() -> {
                    if (chatListAdapter != null)
                        chatListAdapter.updateList(activeChatSessionModel);
                }, 500);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getChatSessionNetworkCall() {
        if (isRefreshing)
            return;
        isRefreshing = true;
        AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).getActiveChatSession(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken()), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                if (isSuccess) {
                    activeChatSessionModel = (ActiveChatSessionModel) data;
                    activeChatSessionModel.sort();
                    showSessionList();
                }
                isRefreshing = false;
            }

            @Override
            public void failed() {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                isRefreshing = false;
            }
        });
    }

    private void getSearchNetworkCall(String text) {
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).searchRoom(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), text), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                if (isSuccess) {
                    activeChatSessionModel = (ActiveChatSessionModel) data;
                    activeChatSessionModel.sort();
                    showSessionList();
                }
            }

            @Override
            public void failed() {
                AppDialogs.getInstance().showProgressBar(context, null, false);
            }
        });
    }
}
