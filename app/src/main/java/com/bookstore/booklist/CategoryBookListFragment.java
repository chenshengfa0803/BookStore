package com.bookstore.booklist;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.bookstore.bookdetail.BookDetailFragment;
import com.bookstore.bookparser.BookCategory;
import com.bookstore.connection.BookInfoConnection;
import com.bookstore.connection.BookInfoUrlBase;
import com.bookstore.connection.ChineseLibraryURL;
import com.bookstore.main.BookOnClickListener;
import com.bookstore.main.FloatButton;
import com.bookstore.main.MainActivity;
import com.bookstore.main.R;
import com.bookstore.main.SubFloatButton;
import com.bookstore.main.animation.BookDetailTransition;
import com.bookstore.provider.BookProvider;
import com.bookstore.provider.BookSQLiteOpenHelper;
import com.bookstore.provider.DB_Column;
import com.bookstore.qr_codescan.ScanActivity;
import com.bookstore.util.BitmapUtil;
import com.bookstore.util.SystemBarTintManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/4/6.
 */
public class CategoryBookListFragment extends Fragment {
    public static final String ARGS_CATEGORY_CODE = "category_code";
    private final static int SCANNING_REQUEST_CODE = 1;
    static int deleteCount = 0;
    GridView mGridView;
    private Activity mActivity;
    private int mCategoryCode = 0;
    private CategoryBookGridViewAdapter gridViewAdapter = null;
    private BookListLoader mlistLoader = null;
    private BookListLoadListener mLoadListener = null;
    private UpdateBookCategoryTask updateTask = null;
    private ObjectAnimator refreshAnimator;
    private boolean isSelectionMode = false;
    private TextView selectedText = null;
    private CheckBox checkAll = null;

    private BookOnClickListener mListener = new BookOnClickListener() {
        @Override
        public void onBookClick(View clickedImageView, String book_id, int category_code) {
            Bitmap bitmap = null;
            int paletteColor = getResources().getColor(android.R.color.darker_gray);
            BitmapDrawable bd = (BitmapDrawable) ((ImageView) clickedImageView).getDrawable();
            if (bd != null) {
                bitmap = bd.getBitmap();
                paletteColor = BitmapUtil.getPaletteColor(bitmap);
            }
            BookDetailFragment detailFragment = BookDetailFragment.newInstance(book_id, category_code, paletteColor);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                detailFragment.setSharedElementEnterTransition(new BookDetailTransition());
                setExitTransition(new Fade());
                detailFragment.setEnterTransition(new Fade());
                detailFragment.setSharedElementReturnTransition(new BookDetailTransition());
            }

            FragmentManager fragmentManager = ((AppCompatActivity) mActivity).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.addSharedElement(clickedImageView, getString(R.string.image_transition))
                    .hide(fragmentManager.findFragmentByTag(CategoryBookListFragment.class.getSimpleName()))
                    .addToBackStack(null)
                    .add(R.id.container_view, detailFragment, BookDetailFragment.class.getSimpleName())
                    .commit();

        }
    };
    private ActionMode.Callback mCallback = new ActionMode.Callback() {
        DeteleSelectedBooksTask deleteTask = null;
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            isSelectionMode = true;
            MenuInflater inflater = mActivity.getMenuInflater();
            inflater.inflate(R.menu.selection_actionmode_delete, menu);

            View actionModeView = LayoutInflater.from(mActivity).inflate(R.layout.selection_actionmode_view, null, false);
            View select_all = actionModeView.findViewById(R.id.select_all);
            selectedText = (TextView) select_all.findViewById(R.id.select_count);
            checkAll = (CheckBox) select_all.findViewById(R.id.select_all_checkbox);
            checkAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkAll.isChecked()) {
                        gridViewAdapter.selectAllItems();
                    } else {
                        gridViewAdapter.clearSelectedItems();
                    }
                    selectedText.setText(gridViewAdapter.getSelectedCount() + "");
                    gridViewAdapter.notifyDataSetChanged();
                }
            });
            mode.setCustomView(select_all);

            gridViewAdapter.setSelectionMode(true);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            selectedText.setText(gridViewAdapter.getSelectedCount() + "");
            if (gridViewAdapter.getCount() == gridViewAdapter.getSelectedCount()) {
                if (checkAll != null) {
                    checkAll.setChecked(true);
                }
            }
            return true;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.select_delete:
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    String alertTitle = getResources().getString(R.string.delete_selected_alert_title);
                    builder.setTitle(alertTitle);
                    builder.setPositiveButton(R.string.positive_OK, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            deleteTask = new DeteleSelectedBooksTask(mode);
                            deleteTask.execute();
                        }
                    });
                    builder.setNegativeButton(R.string.negative_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            gridViewAdapter.setSelectionMode(false);
            isSelectionMode = false;
            if (deleteTask != null && deleteTask.getStatus() == AsyncTask.Status.RUNNING) {
            } else {
                gridViewAdapter.clearSelectedItems();
            }
        }
    };

    public static CategoryBookListFragment newInstance(int category_code) {
        CategoryBookListFragment fragment = new CategoryBookListFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_CATEGORY_CODE, category_code);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mCategoryCode = getArguments().getInt(ARGS_CATEGORY_CODE, 0);
        Log.i("BookStore", "read books by category code " + mCategoryCode);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View category_fragment = null;
        category_fragment = inflater.inflate(R.layout.category_list_fragment, null);
        //((MainActivity) mActivity).getResideMenu().addIgnoredView(category_fragment);

        AppCompatActivity mAppCompatActivity = (AppCompatActivity) mActivity;
        Toolbar category_toolbar = (Toolbar) category_fragment.findViewById(R.id.category_toolbar);
        if (category_toolbar != null) {
            mAppCompatActivity.setSupportActionBar(category_toolbar);
            category_toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
            category_toolbar.setTitleTextColor(Color.WHITE);
            category_toolbar.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            category_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.onBackPressed();
                }
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                SystemBarTintManager tintManager = new SystemBarTintManager(mActivity);
                tintManager.setStatusBarTintEnabled(true);
                tintManager.setTintColor(getResources().getColor(android.R.color.darker_gray));
            }
            category_toolbar.setTitle("");
            TextView title_middle = (TextView) category_toolbar.findViewById(R.id.toolbar_middle_title);
            title_middle.setVisibility(View.VISIBLE);
            title_middle.setText(BookCategory.getCategoryName(mCategoryCode));
            title_middle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mGridView != null) {
                        mGridView.smoothScrollToPosition(0);
                    }
                }
            });
        }
        if (mCategoryCode == 0) {
            TextView textView = new TextView(mActivity);
            textView.setTextColor(Color.RED);
            textView.setText(R.string.category_error);
            FrameLayout frameLayout = (FrameLayout) category_fragment.findViewById(R.id.category_summary);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            frameLayout.addView(textView, lp);
            frameLayout.setVisibility(View.VISIBLE);
        }

        mGridView = (GridView) category_fragment.findViewById(R.id.category_book_gridview);
        gridViewAdapter = new CategoryBookGridViewAdapter(mActivity);
        mGridView.setAdapter(gridViewAdapter);
        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                toggleSelection(position);
                return true;
            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isSelectionMode()) {
                    toggleSelection(position);
                    return;
                }
                final ImageView book_cover = (ImageView) view.findViewById(R.id.book_cover);
                ArrayList<CategoryBookGridViewAdapter.CloudItem> list = gridViewAdapter.getDataList();
                mListener.onBookClick(book_cover, list.get(position).objectId, list.get(position).category_code);
            }
        });
        updateFloatButton();
        return category_fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        //refreshList();
        loadListFromCloud();
    }

    private void refreshList() {
        String selection = null;
        if (mCategoryCode != 'a') {
            selection = DB_Column.BookInfo.CATEGORY_CODE
                    + "="
                    + mCategoryCode;
        }
        String[] projection = {DB_Column.BookInfo.ID, DB_Column.BookInfo.IMG_LARGE, DB_Column.BookInfo.TITLE, DB_Column.BookInfo.CATEGORY_CODE};
        //mlistLoader = new BookListLoader(mActivity, BookProvider.BOOKINFO_URI, projection, selection, null, DB_Column.BookInfo.ID + " DESC LIMIT 15");
        mlistLoader = new BookListLoader(mActivity, BookProvider.BOOKINFO_URI, projection, selection, null, DB_Column.BookInfo.ID + " DESC");
        mLoadListener = new BookListLoadListener();
        mlistLoader.registerListener(0, mLoadListener);
        mlistLoader.startLoading();
    }

    private void loadListFromCloud() {
        AVQuery<AVObject> query;
        AVQuery<AVObject> userQuery = new AVQuery<>(BookSQLiteOpenHelper.BOOKINFO_TABLE_NAME);
        userQuery.whereEqualTo("userId", MainActivity.getCurrentUserId());

        AVQuery<AVObject> categoryQuery = new AVQuery<>(BookSQLiteOpenHelper.BOOKINFO_TABLE_NAME);
        categoryQuery.whereEqualTo(DB_Column.BookInfo.CATEGORY_CODE, mCategoryCode);

        if (mCategoryCode == 'a') {
            query = userQuery;
        } else {
            query = AVQuery.and(Arrays.asList(userQuery, categoryQuery));
        }
        query.limit(1000);
        //query.selectKeys(Arrays.asList("objectId", DB_Column.BookInfo.IMG_LARGE, DB_Column.BookInfo.TITLE, DB_Column.BookInfo.CATEGORY_CODE));
        query.orderByDescending("objectId");

        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        getActivity().getSupportFragmentManager().popBackStack();
                        return;
                    }
                    gridViewAdapter.registerCloudData(list);
                    gridViewAdapter.notifyDataSetChanged();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                SystemBarTintManager tintManager = new SystemBarTintManager(mActivity);
                tintManager.setStatusBarTintEnabled(true);
                tintManager.setTintColor(getResources().getColor(android.R.color.darker_gray));
            }
            //refreshList();
            loadListFromCloud();
            updateFloatButton();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (updateTask != null && updateTask.getStatus() == AsyncTask.Status.RUNNING) {
            updateTask.cancel(true);
        }
    }

    public void updateFloatButton() {
        final FloatButton mainFloatButton = ((MainActivity) mActivity).getFloatButton();

        ImageView imageView = new ImageView(mActivity);
        if (mCategoryCode == 0) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_autorenew_black));
        } else {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.main_floatbutton_add));
        }
        mainFloatButton.setFloatButtonIcon(imageView);

        if (mCategoryCode != 0) {
            int size = getResources().getDimensionPixelSize(R.dimen.sub_float_button_icon_size);
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(size, size);
            params.leftMargin = getResources().getDimensionPixelSize(R.dimen.sub_float_button_margin_left);
            params.topMargin = getResources().getDimensionPixelSize(R.dimen.sub_float_button_margin_top);
            SubFloatButton subFab_camera = new SubFloatButton(mActivity, getResources().getDrawable(R.drawable.sub_floatbutton_camera), params);
            SubFloatButton subFab_chat = new SubFloatButton(mActivity, getResources().getDrawable(R.drawable.sub_floatbutton_chat), params);
            SubFloatButton subFab_location = new SubFloatButton(mActivity, getResources().getDrawable(R.drawable.sub_floatbutton_location), params);
            subFab_camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(mActivity, ScanActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(intent, SCANNING_REQUEST_CODE);
                    mainFloatButton.closeMenu();
                }
            });
            int startAngle = 270;//270 degree
            int endAngle = 360;//360 degree
            int menu_radio = getResources().getDimensionPixelSize(R.dimen.action_menu_radius);
            int menu_duration = 500;//500 ms

            mainFloatButton.createFloatButtonMenu(startAngle, endAngle, menu_radio, menu_duration)
                    .addSubFloatButton(subFab_camera)
                    .addSubFloatButton(subFab_chat)
                    .addSubFloatButton(subFab_location);

            mainFloatButton.addMenuStateListener(new FloatButton.MenuStateListener() {
                @Override
                public void onMenuOpened(FloatButton fb) {
                    ((MainActivity) mActivity).makeBlurWindow();
                    fb.getContentView().setRotation(0);
                    PropertyValuesHolder rotation = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                    ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(fb.getContentView(), rotation);
                    animator.start();
                }

                @Override
                public void onMenuClosed(FloatButton fb) {
                    fb.getContentView().setRotation(45);
                    PropertyValuesHolder rotation = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                    ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(fb.getContentView(), rotation);
                    animator.start();
                    ((MainActivity) mActivity).disappearBlurWindow();
                }
            });
        }

        mainFloatButton.registerClickListener(new FloatButton.FloatButtonClickListener() {
            @Override
            public void onFloatButtonClick(View floatButton) {
                if (mCategoryCode == 0) {
                    updateBookCategory();
                } else {
                    if (mainFloatButton.isMenuOpened()) {
                        mainFloatButton.closeMenu();
                    } else {
                        mainFloatButton.openMenu();
                    }
                }
            }
        });


    }

    public void updateBookCategory() {
        if (updateTask != null) {
            if (updateTask.getStatus() == AsyncTask.Status.RUNNING) {
                return;
            }
        }
        updateTask = new UpdateBookCategoryTask();
        updateTask.execute();
    }

    public boolean isSelectionMode() {
        return isSelectionMode;
    }

    private void toggleSelection(int position) {
        gridViewAdapter.updateSelectedItems(position);
        if (gridViewAdapter.getCount() == gridViewAdapter.getSelectedCount()) {
            if (checkAll != null) {
                checkAll.setChecked(true);
            }
        } else {
            if (checkAll != null) {
                checkAll.setChecked(false);
            }
        }
        if (isSelectionMode()) {
            selectedText.setText(gridViewAdapter.getSelectedCount() + "");
            gridViewAdapter.notifyDataSetChanged();
        } else {
            AppCompatActivity compatActivity = (AppCompatActivity) mActivity;
            compatActivity.startSupportActionMode(mCallback);
        }
    }

    private class BookListLoadListener implements Loader.OnLoadCompleteListener<Cursor> {
        public BookListLoadListener() {
        }

        @Override
        public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
            if (data.getCount() == 0) {
                data.close();
                getActivity().getSupportFragmentManager().popBackStack();
                return;
            }
            gridViewAdapter.registerDataCursor(data);
            gridViewAdapter.notifyDataSetChanged();
        }
    }

    private class UpdateBookCategoryTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            FloatButton mainFloatButton = ((MainActivity) mActivity).getFloatButton();
            mainFloatButton.getContentView().setRotation(0);
            PropertyValuesHolder rotation = PropertyValuesHolder.ofFloat(View.ROTATION, 360);
            refreshAnimator = ObjectAnimator.ofPropertyValuesHolder(mainFloatButton.getContentView(), rotation);
            refreshAnimator.setRepeatMode(ValueAnimator.RESTART);
            refreshAnimator.setRepeatCount(ValueAnimator.INFINITE);
            refreshAnimator.setDuration(1000);
            refreshAnimator.start();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<CategoryBookGridViewAdapter.CloudItem> dataList = gridViewAdapter.getDataList();
            int size = 0;
            for (int pos = 0; pos < (size = dataList.size()); pos++) {
                CategoryBookGridViewAdapter.CloudItem item = dataList.get(pos);

                ChineseLibraryURL clc_url = new ChineseLibraryURL(item.isbn13);
                String url = clc_url.getRequestUrl(BookInfoUrlBase.REQ_CATEGORY);
                BookInfoConnection connection = new BookInfoConnection();
                try {
                    String clcStr = connection.doRequestFromUrl(url);

                    String clcNum = BookCategory.getClcNumber(clcStr);
                    int category_code = BookCategory.getCategoryByClcNum(clcNum);

                    if (category_code != mCategoryCode) {
                        //ContentValues contentValues = new ContentValues();
                        //contentValues.put(DB_Column.BookInfo.CATEGORY_CODE, category_code);
                        //contentValues.put(DB_Column.BookInfo.CLC_NUMBER, clcNum);
                        //mActivity.getContentResolver().update(uri, contentValues, null, null);
                        AVObject bookItem = AVObject.createWithoutData(BookSQLiteOpenHelper.BOOKINFO_TABLE_NAME, item.objectId);
                        bookItem.put(DB_Column.BookInfo.CATEGORY_CODE, category_code);
                        bookItem.put(DB_Column.BookInfo.CLC_NUMBER, clcNum);
                        bookItem.saveInBackground();
                        publishProgress(pos);
                        pos--;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            gridViewAdapter.removeData(values[0]);
            gridViewAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (refreshAnimator != null) {
                refreshAnimator.setRepeatCount(0);
                Toast.makeText(mActivity, "刷新完成", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class DeteleSelectedBooksTask extends AsyncTask<Void, Integer, Integer> {
        private ActionMode actionMode;

        public DeteleSelectedBooksTask(ActionMode actionMode) {
            this.actionMode = actionMode;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            HashSet<Long> selectItems = gridViewAdapter.getSelectedItems();
            deleteCount = 0;
            final int SeletedCount = gridViewAdapter.getSelectedCount();
            Iterator<Long> iterator = selectItems.iterator();
            while (iterator.hasNext()) {
                long itemPos = iterator.next();
                ArrayList<CategoryBookGridViewAdapter.CloudItem> list = gridViewAdapter.getDataList();
                final CategoryBookGridViewAdapter.CloudItem item = list.get((int) itemPos);
                //Uri uri = Uri.parse("content://" + BookProvider.AUTHORITY + "/" + BookSQLiteOpenHelper.BOOKINFO_TABLE_NAME + "/" + item.book_id);
                //mActivity.getContentResolver().delete(uri, null, null);
                AVQuery<AVObject> query = new AVQuery<>(BookSQLiteOpenHelper.BOOKINFO_TABLE_NAME);
                query.getInBackground(item.objectId, new GetCallback<AVObject>() {
                    @Override
                    public void done(AVObject avObject, AVException e) {
                        avObject.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    deleteCount++;
                                    if (deleteCount == SeletedCount) {
                                        loadListFromCloud();
                                    }
                                } else {
                                    e.printStackTrace();
                                    String deleteFail = getResources().getString(R.string.deleted_books_fail, item.title);
                                    Toast.makeText(mActivity, deleteFail, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
            return SeletedCount;
        }

        @Override
        protected void onPostExecute(Integer deletedCount) {
            super.onPostExecute(deletedCount);
            String deletedInfo = getResources().getString(R.string.deleted_books_toast, deletedCount);
            Toast.makeText(mActivity, deletedInfo, Toast.LENGTH_SHORT).show();
            gridViewAdapter.clearSelectedItems();
            actionMode.finish();
            //refreshList();
        }
    }
}
