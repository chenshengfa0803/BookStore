package com.bookstore.main.UserInfo;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVUser;
import com.bookstore.main.R;

/**
 * Created by Administrator on 2016/4/25.
 */
public class MyDialogFragment extends DialogFragment {
    public static final String ARGS_DIALOG_TYPE = "fragmentType";
    public static final int DIALOG_TYPE_USERIMAGE = 0;
    public static final int DIALOG_TYPE_USERNAME = 1;
    public static final int DIALOG_TYPE_USERSIGN = 2;
    public static final int DIALOG_TYPE_USERLOCATION = 3;

    private int mDialogType = -1;
    private View mDialogContainer = null;
    private UserInfoEditFragment.UserInfoListener mListener;

    public static MyDialogFragment newInstance(Bundle arg) {
        MyDialogFragment fragment = new MyDialogFragment();
        Bundle args = arg;
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDialogType = getArguments().getInt(ARGS_DIALOG_TYPE, 0);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        mDialogContainer = inflater.inflate(R.layout.dialog_fragment, null);

//        TextView text = (TextView) container.findViewById(R.id.dialog_text);
//        text.setText(R.string.dialog_text);
//        text.setSelected(true);
//        ImageView image = (ImageView) container.findViewById(R.id.dialog_image);
//        image.setImageDrawable(getResources().getDrawable(R.drawable.zhifubao));
        switch (mDialogType) {
            case DIALOG_TYPE_USERNAME:
                initEditUserName();
                break;
            case DIALOG_TYPE_USERSIGN:
                initEditSign();
                break;
        }


        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(mDialogContainer);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;

        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        return dialog;
    }

    private void initEditUserName() {
        View userNameDialog = mDialogContainer.findViewById(R.id.edit_username);
        String nickName = getArguments().getString("nickName");
        final EditText nameText = (EditText) userNameDialog.findViewById(R.id.edit_nick_name);
        nameText.setText(nickName);
        nameText.setSelection(nickName.length());
        userNameDialog.setVisibility(View.VISIBLE);

        Button cancel = (Button) userNameDialog.findViewById(R.id.cancel_edit);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialogFragment.this.dismiss();
            }
        });

        Button okBtn = (Button) userNameDialog.findViewById(R.id.ok_edit);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AVUser avUser = AVUser.getCurrentUser();
                String name = nameText.getText().toString().trim();
                avUser.put("nickName", name);
                avUser.saveInBackground();
                if (mListener != null) {
                    mListener.onNickNameChange(name);
                }
                MyDialogFragment.this.dismiss();
            }
        });
    }

    private void initEditSign() {
        View signDialog = mDialogContainer.findViewById(R.id.edit_sign);
        String sign = getArguments().getString("sign");
        final EditText signText = (EditText) signDialog.findViewById(R.id.edit_sign_text);
        signText.setText(sign);
        signText.setSelection(sign.length());
        signDialog.setVisibility(View.VISIBLE);

        Button cancel = (Button) signDialog.findViewById(R.id.cancel_edit_sign);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialogFragment.this.dismiss();
            }
        });

        Button okBtn = (Button) signDialog.findViewById(R.id.ok_edit_sign);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AVUser avUser = AVUser.getCurrentUser();
                String sign = signText.getText().toString().trim();
                avUser.put("userSignature", sign);
                avUser.saveInBackground();
                if (mListener != null) {
                    mListener.onSignChange(sign);
                }
                MyDialogFragment.this.dismiss();
            }
        });
    }

    public void registerUserInfoListener(UserInfoEditFragment.UserInfoListener listener) {
        mListener = listener;
    }
}
