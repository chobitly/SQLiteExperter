package org.chobitly.sqliteexporter;

import android.annotation.TargetApi;
import android.graphics.Outline;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExportFragment extends Fragment {
    private static final long INVALID_ITEM_ID = -1;

    private long mItemID = INVALID_ITEM_ID;

    View mFAB;

    public ExportFragment() {
        // Required empty public constructor
    }

    public void setItemID(long itemID) {
        this.mItemID = itemID;
        // TODO 更新界面内容
    }

    public void export() {
        // TODO 导出
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View contentView = inflater.inflate(R.layout.fragment_export, container, false);
        mFAB = contentView.findViewById(R.id.fab);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupFAB();
        }
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                export();
            }
        });
        return contentView;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupFAB() {
        mFAB.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // Or read size directly from the view's width/height
                int size = getResources().getDimensionPixelSize(R.dimen.activity_fab_size);
                outline.setOval(0, 0, size, size);
            }
        });
        mFAB.setClipToOutline(true);
        mFAB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getActionMasked();
                    /* Raise view on ACTION_DOWN and lower it on ACTION_UP. */
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mFAB.setTranslationZ(getResources().getDimensionPixelSize(R.dimen.activity_fab_translation_z));
                        break;
                    case MotionEvent.ACTION_UP:
                        mFAB.setTranslationZ(0);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

}
