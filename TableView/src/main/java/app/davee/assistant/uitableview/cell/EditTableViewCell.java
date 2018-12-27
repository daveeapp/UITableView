package app.davee.assistant.uitableview.cell;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.RelativeLayout;

import app.davee.assistant.uitableview.R;
import app.davee.assistant.uitableview.UITableViewCell;
import app.davee.assistant.uitableview.DimensionUtils;

import app.davee.assistant.uitableview.UITableViewCell;

/**
 * EditTableViewCell
 * <p>
 * Created by davee 2018/5/7.
 * Copyright (c) 2018 davee. All rights reserved.
 */
public class EditTableViewCell extends UITableViewCell<EditTableViewCellModel> {
    
    public static final int VIEW_TYPE = app.davee.assistant.uitableview.R.id.uitableview_cell_viewType_edit;
    
    private EditText mEditText;
    
    // private InputFilter mLimitByteInputFilter = new InputFilter() {
    //     public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
    //         if (source != null && EditTableViewCell.this.mModel != null && ((EditTableViewCellModel)EditTableViewCell.this.mModel).limitByte > 0) {
    //             byte[] bytes = source.toString().getBytes();
    //             int destLength = dest == null ? 0 : dest.toString().getBytes().length;
    //             int length = bytes.length + destLength;
    //             if (length > ((EditTableViewCellModel)EditTableViewCell.this.mModel).limitByte) {
    //                 int diff = ((EditTableViewCellModel)EditTableViewCell.this.mModel).limitByte - destLength;
    //                 byte[] result = new byte[diff];
    //                 System.arraycopy(bytes, 0, result, 0, result.length);
    //                 return new String(result, 0, result.length);
    //             }
    //         }
    //
    //         return source;
    //     }
    // };
    
    private TextWatcher mTextChangedListener = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        
        public void afterTextChanged(Editable s) {
            if (mModel != null) {
                mModel.setDetailText(s.toString());
            }
            
        }
    };
    
    public EditTableViewCell(Context context, int viewType) {
        super(context, viewType);
        this.initialize();
    }
    
    public EditTableViewCell(Context context) {
        super(context);
        this.initialize();
    }
    
    public EditTableViewCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }
    
    public EditTableViewCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }
    
    private void initialize() {
        this.mEditText.addTextChangedListener(this.mTextChangedListener);
        // this.mEditText.setFilters(new InputFilter[]{this.mLimitByteInputFilter});
    }
    
    protected void createLayoutForCustom() {
        this.mTitleTextView = super.newTitleTextView();
        this.mEditText = this.newEditText();
        this.mEditText.setId(app.davee.assistant.uitableview.R.id.uitableview_cell_viewType_edit);
        
        ContentView.LayoutParams titleParams = new ContentView.LayoutParams(-1, -2);
        titleParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        titleParams.addRule(RelativeLayout.CENTER_VERTICAL);
        titleParams.addRule(RelativeLayout.START_OF, mEditText.getId());
        titleParams.rightMargin = DimensionUtils.dp2px(this.getContext(), 8);
    
        ContentView.LayoutParams editParams = new ContentView.LayoutParams(-2, -2);
        editParams.addRule(RelativeLayout.CENTER_VERTICAL);
        editParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        this.mContentView.addView(this.mTitleTextView, titleParams);
        this.mContentView.addView(this.mEditText, editParams);
    }
    
    protected void setModelInternal(EditTableViewCellModel model) {
        super.setModelInternal(model);
        if (model != null) {
            mEditText.setHint(model.editHint);
            mEditText.setText(model.getDetailText());
            mEditText.setFilters(model.getInputFilters());
        }
    }
    
    private EditText newEditText() {
        EditText editText = new EditText(this.getContext());
        editText.setBackground(null);
        editText.setMinWidth(DimensionUtils.dp2px(this.getContext(), 64));
        editText.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            editText.setTextAppearance(app.davee.assistant.uitableview.R.style.UITableView_TextAppearance_Title);
        } else {
            editText.setTextAppearance(getContext(), app.davee.assistant.uitableview.R.style.UITableView_TextAppearance_Title);
        }
        editText.setPadding(0, 0, 0, 0);
        return editText;
    }
    
    public EditText getEditText() {
        return mEditText;
    }
}
