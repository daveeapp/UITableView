package app.davee.assistant.uitableview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DimenRes;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import app.davee.assistant.uitableview.cell.ContentView;
import app.davee.assistant.uitableview.models.UITableViewCellModel;
import app.davee.assistant.uitableview.swipe.SwipeActionsConfiguration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static app.davee.assistant.uitableview.UITableViewCell.SwipeLocation.Leading;
import static app.davee.assistant.uitableview.UITableViewCell.SwipeLocation.None;
import static app.davee.assistant.uitableview.UITableViewCell.SwipeLocation.Trailing;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * UITableViewCell
 * <p>
 * 优化记录：
 * 1）不使用ConstraintLayout，在简单的嵌套布局情况下，ConstraintLayout性能较低
 * 2）尽量使用代码布局，xml布局效率低
 * <p>
 * Layout Structure: [Foreground Layout[ContentView + AccessoryView]][Background Layout[Default Null]]
 * <p>
 * Created by davee 2018/2/26.
 * Copyright (c) 2018 davee. All rights reserved.
 */
@SuppressWarnings("unused")
public class UITableViewCell<T extends UITableViewCellModel> extends FrameLayout {
    
    private static final String TAG = "UITableViewCell";
    
    public static final int UNDEFINED = UITableView.UNDEFINED;
    
    public static final int VIEW_TYPE_DEFAULT = R.id.uitableview_cell_viewType_default;
    public static final int VIEW_TYPE_VALUE1 = R.id.uitableview_cell_viewType_value1;
    public static final int VIEW_TYPE_VALUE2 = R.id.uitableview_cell_viewType_value2;
    public static final int VIEW_TYPE_SUBTITLE = R.id.uitableview_cell_viewType_subtitle;
    public static final int VIEW_TYPE_DEFAULT_SELECTABLE = R.id.uitableview_cell_viewType_default_selectable;
    
    //---------------------------------------------------------------
    //              MARK: Enum Defines
    //---------------------------------------------------------------
    
    /// UITableViewCellStyle
    @IntDef({Style.Custom, Style.Default, Style.Value1, Style.Value2, Style.SubTitle, Style.DefaultSelectable})
    @Retention(SOURCE)
    public @interface Style {
        int Custom = 0;
        int Default = 1;
        int Value1 = 2;
        int Value2 = 3;
        int SubTitle = 4;
        int DefaultSelectable = 5;
    }
    
    /// UITableViewCellAccessoryType
    @IntDef({AccessoryType.None, AccessoryType.Custom, AccessoryType.Detail, AccessoryType.Disclosure, AccessoryType.CheckMark})
    @Retention(SOURCE)
    public @interface AccessoryType {
        int None = 0;
        int Custom = 1;
        int Detail = 2;
        int Disclosure = 3;
        int CheckMark = 4;
    }
    
    /// UITableViewCellSelectionStyle
    @IntDef({SelectionStyle.None, SelectionStyle.Custom, SelectionStyle.Gray, SelectionStyle.Blue})
    @Retention(SOURCE)
    public @interface SelectionStyle {
        int None = 0;
        int Custom = 1;
        int Gray = 2;
        int Blue = 3;
    }
    
    /// UITableViewCellSeparatorStyle
    @IntDef({SeparatorStyle.None, SeparatorStyle.SingleLine, SeparatorStyle.SingleLineEtched})
    @Retention(SOURCE)
    public @interface SeparatorStyle {
        int None = 0;
        int SingleLine = 1;
        int SingleLineEtched = 2;
    }
    
    /// UITableViewCellEditingStyle
    @IntDef(value = {EditingStyle.None, EditingStyle.Delete, EditingStyle.Insert})
    @Retention(SOURCE)
    public @interface EditingStyle {
        int None = 0;
        int Delete = 1;
        int Insert = 2;
    }
    
    /// UITableViewCellStateMask
    public @interface StateMask {
        int Normal = 0;
        int ShowingEditingControl = 1;
        int ShowingSwipeAction = 2;
    }
    
    /// Swipe Action Location
    @IntDef({None, Leading, Trailing})
    @Retention(RetentionPolicy.SOURCE)
    @interface SwipeLocation {
        int None = 0;
        int Leading = 1;
        int Trailing = 2;
    }
    
    //---------------------------------------------------------------
    //              Fields
    //---------------------------------------------------------------
    
    @Style
    protected int mCellStyle = Style.Custom;
    
    @AccessoryType
    private int mAccessoryType = AccessoryType.None;
    
    @SelectionStyle
    private int mSelectionStyle = SelectionStyle.Gray;
    
    @SeparatorStyle
    private int mSeparatorStyle = SeparatorStyle.SingleLine;
    
    protected RelativeLayout mForegroundLayout;
    protected RelativeLayout mBackgroundLayout;
    protected ContentView mContentView;
    protected ImageView mImageView; // Not usable for custom style
    protected TextView mTitleTextView;
    protected TextView mDetailTextView; // 在不同style中代表不同功能：SubTitle + Value1 + Value2
    protected ImageView mAccessoryView;
    
    /**
     * Adjust content indent, default 0, in 'px'
     */
    private int mIndentationWidth = 0;
    
    /**
     * The accessory drawable set by user
     */
    private Drawable mUserAccessoryDrawable = null;
    private Drawable mAccessoryDrawableCheckMark = null;
    private Drawable mAccessoryDrawableDetail = null;
    private Drawable mAccessoryDrawableDisclosure = null;
    
    /**
     * The selection drawable set by user
     */
    private Drawable mUserSelectionDrawable;
    
    /// Separator Fields
    
    // boolean mDrawSeparatorOnCell = false;
    // boolean mIsFirstCellInSection = false;
    // boolean mIsLastCellInSection = false;
    private float mSeparatorHeight = 0;
    private int mSeparatorColor = 0;
    private Rect mSeparatorInsets;
    protected Rect mSeparatorDrawableRect;
    protected Drawable mUserSeparatorDrawable;
    private boolean mClipDivider;
    private boolean mSeparatorStartFromImageView = false;
    
    
    /// Editing Fields
    
    private boolean mEditing = false;
    @EditingStyle
    private int mEditingStyle = EditingStyle.None;
    private int mEditingIndentWidth = DimensionUtils.dp2px(getContext(), 16); // 'px'. adjust editing control layout indent. default is 16.
    private Drawable mUserEditingSelectionDrawable;
    private EditingControlLayout mEditingControlLayout;
    private ValueAnimator mOpenEditingAnimator = null;
    private ValueAnimator mCloseEditingAnimator = null;
    private OnClickListener mEditingButtonClickListener = null;
    
    /* Swipe Action */
    @StateMask
    private int mCurState = StateMask.Normal;
    private boolean mSwipeActionEnabled;
    private SwipeActionLayout mLeadingSwipeLayout = null;
    private SwipeActionLayout mTrailingSwipeLayout = null;
    
    private UITableView.TableViewCellHelper mCellHelper = null;
    
    protected T mModel;
    
    //---------------------------------------------------------------
    //              Instantiate
    //---------------------------------------------------------------
    
    @SuppressWarnings("unchecked")
    protected static <T extends UITableViewCell> T instanceFromLayout(Context context, @LayoutRes int layout) {
        View result = LayoutInflater.from(context).inflate(layout, null, false);
        if (UITableViewCell.class.isAssignableFrom(result.getClass())) {
            return (T) result;
        } else {
            return null;
        }
    }
    
    public static boolean isViewTypeMatched(int viewType) {
        return viewType == VIEW_TYPE_DEFAULT
                || viewType == VIEW_TYPE_VALUE1
                || viewType == VIEW_TYPE_VALUE2
                || viewType == VIEW_TYPE_SUBTITLE
                || viewType == VIEW_TYPE_DEFAULT_SELECTABLE;
        
    }
    
    public UITableViewCell(Context context, int viewType) {
        super(context);
        
        if (viewType == VIEW_TYPE_DEFAULT) {
            mCellStyle = Style.Default;
        } else if (viewType == VIEW_TYPE_VALUE1) {
            mCellStyle = Style.Value1;
        } else if (viewType == VIEW_TYPE_VALUE2) {
            mCellStyle = Style.Value2;
        } else if (viewType == VIEW_TYPE_SUBTITLE) {
            mCellStyle = Style.SubTitle;
        } else if (viewType == VIEW_TYPE_DEFAULT_SELECTABLE) {
            mCellStyle = Style.DefaultSelectable;
        } else {
            mCellStyle = Style.Custom;
        }
        
        init(null, 0);
    }
    
    
    //===============================================================
    //              MARK: Constructors
    //===============================================================
    
    public UITableViewCell(Context context) {
        super(context);
        
        init(null, 0);
    }
    
    public UITableViewCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        init(attrs, 0);
    }
    
    public UITableViewCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        
        init(attrs, defStyleAttr);
    }
    
    private void init(AttributeSet attrs, int defStyleAttr) {
        boolean isFromXmlLayout = (attrs != null);
        
        // 1) 初始化布局
        initRootLayout();
        
        // 2)  设置Attrs
        Drawable imageDrawable = null;
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.UITableViewCell, defStyleAttr, 0);
    
            mCellStyle = a.getInteger(R.styleable.UITableViewCell_cellStyle, mCellStyle);
            
            // AccessoryType
            mAccessoryType = a.getInteger(R.styleable.UITableViewCell_cellAccessoryType, mAccessoryType);
            if (a.hasValue(R.styleable.UITableViewCell_cellAccessoryDrawable)) {
                mUserAccessoryDrawable = a.getDrawable(R.styleable.UITableViewCell_cellAccessoryDrawable);
                if (mUserAccessoryDrawable != null) {
                    mAccessoryType = AccessoryType.Custom;
                }
            }
    
            // SelectionStyle
            mSelectionStyle = a.getInteger(R.styleable.UITableViewCell_cellSelectionStyle, SelectionStyle.Gray);
            if (a.hasValue(R.styleable.UITableViewCell_cellSelectionDrawable)) {
                mUserSelectionDrawable = a.getDrawable(R.styleable.UITableViewCell_cellSelectionDrawable);
                if (mUserSelectionDrawable != null) {
                    mSelectionStyle = SelectionStyle.Custom;
                }
            }
    
            imageDrawable = a.getDrawable(R.styleable.UITableViewCell_cellImage);
            
            a.recycle();
        }
        
        // 4) Apply the attrs
        if (mCellStyle != Style.Custom || !isFromXmlLayout) {
            ensureContentView();
            this.applyCellStyle();
        }
        
        if (imageDrawable != null){
            setImageDrawable(imageDrawable);
        }
        this.applyAccessoryType();
        this.applySelectionStyle();
    }
    
    protected void initRootLayout() {
        mForegroundLayout = new RelativeLayout(getContext());
        FrameLayout.LayoutParams lp = new LayoutParams(-1, -2);
        addViewToRoot(mForegroundLayout, 0, lp);
    }
    
    protected void ensureContentView() {
        if (mContentView == null) {
            mContentView = new ContentView(getContext());
            mContentView.setId(R.id.uitableview_cell_contentView);
            addViewToForeground(mContentView, 0, generateContentLayoutParams());
        }
    }
    
    private RelativeLayout.LayoutParams generateContentLayoutParams() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -2);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.leftMargin = mIndentationWidth;
        return params;
    }
    
    private void updateContentViewLayoutParams() {
        if (mContentView == null) {
            return;
        }
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mContentView.getLayoutParams();
        if (params == null) {
            params = generateContentLayoutParams();
        }
        
        // Check whether has start imageView
        // if (isViewDisabled(mImageView)) {
        //     params.removeRule(RelativeLayout.END_OF);
        //     mContentView.setContentInsetsLeftToDefault();
        // } else {
        //     params.addRule(RelativeLayout.END_OF, mImageView.getId());
        //     mContentView.setContentInsetsLeft(0);
        // }
        
        // Check whether has accessoryView
        if (isViewEnabled(mAccessoryView)){
            params.addRule(RelativeLayout.START_OF, mAccessoryView.getId());
            mContentView.setContentInsetsRight(0);
        } else {
            params.removeRule(RelativeLayout.START_OF);
            mContentView.setContentInsetsRightToDefault();
        }
        
        mContentView.setLayoutParams(params);
    }
    
    protected void interceptAddView(View child) {
        if (mCellStyle == Style.Custom && child instanceof ContentView) {
            child.setLayoutParams(generateContentLayoutParams());
            setupContentViewFromXml((ContentView) child);
        }
    }
    
    protected void setupContentViewFromXml(@NonNull ContentView view) {
        if (mContentView != null && mContentView.getParent() == mForegroundLayout) {
            mContentView.removeAllViews();
            mForegroundLayout.removeView(mContentView);
        }
        mContentView = view;
        updateContentViewLayoutParams();
        mForegroundLayout.addView(mContentView);
    }
    
    public ContentView getContentView() {
        return mContentView;
    }
    
    public RelativeLayout getForegroundLayout() {
        return mForegroundLayout;
    }
    
    @Nullable
    public RelativeLayout getBackgroundLayout() {
        return mBackgroundLayout;
    }
    
    public int getIndentationWidth() {
        return mIndentationWidth;
    }
    
    public void setIndentationWidth(int indentationWidth) {
        if (mIndentationWidth != indentationWidth) {
            mIndentationWidth = indentationWidth;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mContentView.getLayoutParams();
            params.leftMargin = mIndentationWidth;
            mContentView.setLayoutParams(params);
        }
    }
    
    public Rect getContentInsets() {
        return mContentView.getContentInsets();
    }
    
    public void setContentInsets(@NonNull Rect insets) {
        mContentView.setContentInsets(insets);
    }
    
    public void setCellHelper(UITableView.TableViewCellHelper cellHelper) {
        mCellHelper = cellHelper;
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // MARK: Custom addView
    ///////////////////////////////////////////////////////////////////////////
    
    protected void addViewToRoot(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
    }
    
    protected void removeViewFromRoot(View child) {
        super.removeView(child);
    }
    
    public void addViewToForeground(View child, int index, ViewGroup.LayoutParams params) {
        mForegroundLayout.addView(child, index, params);
    }
    
    public void removeViewFromForeground(View child) {
        mForegroundLayout.removeView(child);
    }
    
    public void addViewToContentView(View child, int index, ViewGroup.LayoutParams params) {
        mContentView.addView(child, index, params);
    }
    
    public void removeViewFromContentView(View child) {
        mContentView.removeView(child);
    }
    
    // =================================
    // MARK - Configure mCellStyle
    // =================================
    
    @Style
    public int getCellStyle() {
        return mCellStyle;
    }
    
    private void setCellStyle(int cellStyle) {
        if (mCellStyle != cellStyle) {
            mCellStyle = cellStyle;
            applyCellStyle();
        }
    }
    
    /**
     * 根据设置的Style来布局Cell
     */
    protected void applyCellStyle() {
        if (mContentView == null) {
            return;
        }
        mContentView.removeAllViews();
        switch (mCellStyle) {
            case Style.Custom:
                // This method do nothing default
                createLayoutForCustom();
                return;
            case Style.Default:
            case Style.DefaultSelectable:
                createLayoutForStyleDefault();
                break;
            case Style.SubTitle:
                createLayoutForStyleSubtitle();
                break;
            case Style.Value1:
                createLayoutForStyleValue1();
                break;
            case Style.Value2:
                createLayoutForStyleValue2();
                break;
        }
        
        // mTitleTextView = mContentView.findViewById(R.id.uitableview_cell_titleTextView);
        // if (mCellStyle != Style.Default) {
        //     mDetailTextView = mContentView.findViewById(R.id.uitableview_cell_valueTextView);
        // }
    }
    
    // =====================================
    // MARK - Configure Left ImageView
    // =====================================
    
    // public void setImageResource(int imageResource) {
    //     this.setImageDrawable(internalGetDrawable(imageResource));
    // }
    
    public void setImageDrawable(Drawable drawable) {
        if (mCellStyle != Style.Custom){
            enableImageView(drawable != null);
            if (mImageView != null) {
                mImageView.setImageDrawable(drawable);
            }
        }
    }
    
    private void enableImageView(boolean enable) {
        if (enable) {
            if (mImageView == null) {
                mImageView = newStartImageView();
                // addViewToForeground(mImageView, 0, generateImageViewLayoutParams());
                // updateContentViewLayoutParams();
                addViewToContentView(mImageView, 0, generateImageViewLayoutParams());
                updateDefaultLayoutParams();
                return;
            }
            if (mImageView.getParent() == null) {
                // addViewToForeground(mImageView, 0, mImageView.getLayoutParams());
                // updateContentViewLayoutParams();
                addViewToContentView(mImageView, 0, generateImageViewLayoutParams());
                updateDefaultLayoutParams();
            }
        } else {
            if (mImageView != null && mImageView.getParent() == mForegroundLayout) {
                updateDefaultLayoutParams();
                removeFromParent(mImageView);
                mImageView = null;
                // updateContentViewLayoutParams();
            }
        }
    }
    
    private ViewGroup.LayoutParams generateImageViewLayoutParams() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
        params.addRule(RelativeLayout.ALIGN_PARENT_START);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        // params.leftMargin =
        params.rightMargin = DimensionUtils.resolveDp(getContext(), R.dimen.uitableview_cell_contentInsets_start);
        return params;
    }
    
    // =====================================
    // MARK -  Configure AccessoryType
    // =====================================
    
    protected Drawable getAccessoryDrawableDetail() {
        if (mAccessoryDrawableDetail == null) {
            mAccessoryDrawableDetail = internalGetDrawable(R.drawable.uitableview_cell_accessory_detail_24dp);
        }
        return mAccessoryDrawableDetail;
    }
    
    protected Drawable getAccessoryDrawableDisclosure() {
        if (mAccessoryDrawableDisclosure == null) {
            mAccessoryDrawableDisclosure = internalGetDrawable(R.drawable.uitableview_cell_accessory_disclosure_24dp);
        }
        return mAccessoryDrawableDisclosure;
    }
    
    protected Drawable getAccessoryDrawableCheckMark() {
        if (mAccessoryDrawableCheckMark == null) {
            mAccessoryDrawableCheckMark = internalGetDrawable(R.drawable.ic_check_blue_24dp);
        }
        return mAccessoryDrawableCheckMark;
    }
    
    public int getAccessoryType() {
        return mAccessoryType;
    }
    
    public void setAccessoryType(int accessoryType) {
        if (mAccessoryType != accessoryType) {
            mAccessoryType = accessoryType;
            applyAccessoryType();
        }
    }
    
    public void setAccessoryViewClickable(boolean clickable){
        if (mAccessoryView != null){
            if (clickable){
                mAccessoryView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    
                    }
                });
            } else {
                mAccessoryView.setOnClickListener(null);
                mAccessoryView.setClickable(false);
            }
        }
    }
    
    public ImageView getAccessoryView() {
        return mAccessoryView;
    }
    
    protected void applyAccessoryType() {
        switch (mAccessoryType) {
            case AccessoryType.Detail:
                setAccessoryDrawable(getAccessoryDrawableDetail());
                break;
            case AccessoryType.Disclosure:
                setAccessoryDrawable(getAccessoryDrawableDisclosure());
                break;
            case AccessoryType.CheckMark:
                setAccessoryDrawable(getAccessoryDrawableCheckMark());
                break;
            case AccessoryType.Custom:
                if (mUserAccessoryDrawable != null) {
                    setAccessoryDrawable(mUserAccessoryDrawable);
                }
                break;
            case AccessoryType.None:
                setAccessoryViewEnable(false);
                break;
        }
    }
    
    protected void setAccessoryDrawable(Drawable drawable) {
        setAccessoryViewEnable(drawable != null);
        if (mAccessoryView != null) {
            mAccessoryView.setImageDrawable(drawable);
        }
    }
    
    protected void setAccessoryViewEnable(boolean enable) {
        if (enable) {
            if (mAccessoryView == null) {
                mAccessoryView = newAccessoryView();
                addViewToForeground(mAccessoryView, 0, generateAccessoryViewLayoutParams());
                updateContentViewLayoutParams();
                return;
            }
            
            if (mAccessoryView.getParent() == null) {
                addViewToForeground(mAccessoryView, 0, mAccessoryView.getLayoutParams());
                updateContentViewLayoutParams();
            }
            
        } else {
            if (mAccessoryView != null && mAccessoryView.getParent() == mForegroundLayout) {
                //removeViewFromSelf(mAccessoryView);
                removeFromParent(mAccessoryView);
                mAccessoryView = null;
                updateContentViewLayoutParams();
            }
        }
    }
    
    protected ViewGroup.LayoutParams generateAccessoryViewLayoutParams() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
        params.addRule(RelativeLayout.ALIGN_PARENT_END);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.leftMargin = DimensionUtils.dp2px(getContext(), 12);
        params.rightMargin = getDimenPixel(R.dimen.uitableview_common_margin_start);
        return params;
    }
    
    // =====================================
    // MARK - Configure Selection Style
    // =====================================
    
    public int getSelectionStyle() {
        return mSelectionStyle;
    }
    
    public void setSelectionStyle(int selectionStyle) {
        if (mSelectionStyle != selectionStyle) {
            mSelectionStyle = selectionStyle;
            applySelectionStyle();
        }
    }
    
    public Drawable getUserSelectionDrawable() {
        return mUserSelectionDrawable;
    }
    
    public void setUserSelectionDrawable(Drawable userSelectionDrawable) {
        if (userSelectionDrawable != null && mUserSelectionDrawable != userSelectionDrawable) {
            mUserSelectionDrawable = userSelectionDrawable;
            mSelectionStyle = SelectionStyle.Custom;
            applySelectionStyle();
        }
    }
    
    public Drawable getUserEditingSelectionDrawable() {
        return mUserEditingSelectionDrawable;
    }
    
    public void setUserEditingSelectionDrawable(Drawable userEditingSelectionDrawable) {
        mUserEditingSelectionDrawable = userEditingSelectionDrawable;
    }
    
    protected void applySelectionStyle() {
        switch (mSelectionStyle) {
            case SelectionStyle.Blue:
                mForegroundLayout.setBackgroundResource(R.drawable.uitableview_cell_selectionstyle_blue);
                break;
            case SelectionStyle.Gray:
                mForegroundLayout.setBackgroundResource(R.drawable.uitableview_cell_selectionstyle_gray);
                break;
            case SelectionStyle.None:
                mForegroundLayout.setBackgroundResource(R.drawable.uitableview_cell_selectionstyle_none);
                break;
            case SelectionStyle.Custom:
                mForegroundLayout.setBackground(mUserSelectionDrawable);
                break;
        }
    }
    
    
    ///////////////////////////////////////////////////////////////////////////
    // Set Title & Values
    ///////////////////////////////////////////////////////////////////////////
    
    @Nullable
    public TextView getTitleTextView() {
        return mTitleTextView;
    }
    
    @Nullable
    public CharSequence getTitleText() {
        if (mTitleTextView != null) {
            return mTitleTextView.getText();
        }
        return null;
    }
    
    public void setTitleText(@StringRes int stringRes) {
        setTitleText(getResources().getText(stringRes));
    }
    
    public void setTitleText(CharSequence text) {
        if (mTitleTextView != null) {
            if (!TextUtils.equals(mTitleTextView.getText(), text)) {
                mTitleTextView.setText(text);
            }
        }
    }
    
    @Nullable
    public TextView getDetailTextView() {
        return mDetailTextView;
    }
    
    @Nullable
    public CharSequence getDetailText() {
        if (mDetailTextView != null) {
            return mDetailTextView.getText();
        }
        return null;
    }
    
    public void setDetailText(CharSequence text) {
        if (mDetailTextView != null) {
            if (!TextUtils.equals(mDetailTextView.getText(), text)) {
                mDetailTextView.setText(text);
            }
        }
    }
    
    public void setDetailText(@StringRes int text) {
        setDetailText(getResources().getText(text));
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // Draw UITableViewCell Divider
    ///////////////////////////////////////////////////////////////////////////
    @SeparatorStyle
    public int getSeparatorStyle() {
        return mSeparatorStyle;
    }
    
    public void setSeparatorStyle(@SeparatorStyle int separatorStyle) {
        mSeparatorStyle = separatorStyle;
    }
    
    public Rect getSeparatorInsets() {
        return mSeparatorInsets;
    }
    
    public void setSeparatorInsets(Rect separatorInsets) {
        mSeparatorInsets = separatorInsets;
    }
    
    public float getSeparatorHeight() {
        return mSeparatorHeight;
    }
    
    public void setSeparatorHeight(float separatorHeight) {
        mSeparatorHeight = separatorHeight;
    }
    
    public int getSeparatorColor() {
        return mSeparatorColor;
    }
    
    public void setSeparatorColor(int separatorColor) {
        mSeparatorColor = separatorColor;
    }
    
    public void setUserSeparatorDrawable(Drawable userSeparatorDrawable) {
        if (userSeparatorDrawable != null) {
            mSeparatorHeight = userSeparatorDrawable.getIntrinsicHeight();
            mClipDivider = userSeparatorDrawable instanceof ColorDrawable;
        } else {
            mSeparatorHeight = 0; // reset to default
            mClipDivider = false;
        }
        
        mUserSeparatorDrawable = userSeparatorDrawable;
    }
    
    public boolean isSeparatorStartFromImageView() {
        return mSeparatorStartFromImageView;
    }
    
    public void setSeparatorStartFromImageView(boolean separatorStartFromImageView) {
        mSeparatorStartFromImageView = separatorStartFromImageView;
    }
    
    // ==========================
    // MARK - Draw Separator
    // ==========================
    
    // public void setDrawSeparatorOnCell(boolean drawSeparatorOnCell) {
    //     setWillNotDraw(!mDrawSeparatorOnCell);
    //     if (mDrawSeparatorOnCell != drawSeparatorOnCell) {
    //         mDrawSeparatorOnCell = drawSeparatorOnCell;
    //         if (mDrawSeparatorOnCell){
    //             ensureSeparatorPaint();
    //         }
    //     }
    // }
    //
    // @SuppressWarnings("SuspiciousNameCombination")
    // private void ensureSeparatorPaint() {
    //     if (mSeparatorPaint == null){
    //         mSeparatorPaint = new Paint();
    //         mSeparatorPaint.setAntiAlias(true);
    //         mSeparatorPaint.setColor(mSeparatorColor);
    //         mSeparatorPaint.setStyle(Paint.Style.FILL);
    //         mSeparatorPaint.setStrokeWidth(mSeparatorHeight);
    //     }
    //     // return mSeparatorPaint;
    // }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // if (mDrawSeparatorOnCell){
        //     if (mIsFirstCellInSection){
        //         drawSectionHeaderDivider(canvas, mSeparatorPaint);
        //     }
        //     if (mIsLastCellInSection){
        //         drawSectionFooterDivider(canvas, mSeparatorPaint);
        //     } else {
        //         drawCellSeparator(canvas, mSeparatorPaint);
        //     }
        // }
    }
    
    @SuppressWarnings("SuspiciousNameCombination")
    protected void configureSeparatorPaint(@NonNull final Paint separatorPaint) {
        if (mSeparatorHeight != 0 && mSeparatorHeight != separatorPaint.getStrokeWidth()) {
            separatorPaint.setStrokeWidth(mSeparatorHeight);
        }
        if (mSeparatorColor != 0 && mSeparatorColor != separatorPaint.getColor()) {
            separatorPaint.setColor(mSeparatorColor);
        }
    }
    
    boolean isShowingSeparator() {
        return mSeparatorStyle != SeparatorStyle.None;
    }
    
    int getSeparatorStartX() {
        int startX = this.getLeft();
        if (mSeparatorStyle == SeparatorStyle.SingleLine) {
            startX += mContentView.getLeft() + mContentView.getPaddingLeft() + mContentView.getTranslationX();
            if (isViewEnabled(mImageView) && !mSeparatorStartFromImageView){
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mImageView.getLayoutParams();
                startX += mImageView.getWidth() + params.rightMargin;
            }
        }
        if (mSeparatorInsets != null) {
            startX += mSeparatorInsets.left;
        }
        return startX;
    }
    
    int getSeparatorStopX() {
        int stopX = this.getRight();
        if (mSeparatorInsets != null) {
            stopX -= mSeparatorInsets.right;
        }
        return stopX;
    }
    
    Rect calcDividerDrawableRect() {
        if (mSeparatorDrawableRect == null) {
            mSeparatorDrawableRect = new Rect();
        }
        
        mSeparatorDrawableRect.left = this.getLeft();
        mSeparatorDrawableRect.right = this.getRight();
        if (mSeparatorStyle == SeparatorStyle.SingleLine) {
            mSeparatorDrawableRect.left += mContentView.getLeft() + mContentView.getPaddingLeft();
        }
        if (mSeparatorInsets != null) {
            mSeparatorDrawableRect.left += mSeparatorInsets.left;
            mSeparatorDrawableRect.right -= mSeparatorInsets.right;
        }
        mSeparatorDrawableRect.top = (int) (getBottom() - mSeparatorHeight);
        mSeparatorDrawableRect.bottom = getBottom();
        
        return mSeparatorDrawableRect;
    }
    
    protected void drawCellSeparator(final Canvas canvas, final Paint separatorPaint) {
        if (!isShowingSeparator()) {
            return;
        }
        
        if (mUserSeparatorDrawable == null) {
            configureSeparatorPaint(separatorPaint);
            int startX = getSeparatorStartX();
            int stopX = getSeparatorStopX();
            int y = this.getBottom();
            canvas.drawLine(startX, y, stopX, y, separatorPaint);
        } else {
            final Drawable divider = mUserSeparatorDrawable;
            final boolean clipDivider = mClipDivider;
            
            final Rect bounds = calcDividerDrawableRect();
            if (clipDivider) {
                canvas.save();
                canvas.clipRect(bounds);
            } else {
                divider.setBounds(bounds);
            }
            divider.draw(canvas);
            if (clipDivider) {
                canvas.restore();
            }
        }
    }
    
    protected void drawSectionHeaderDivider(final Canvas canvas, final Paint separatorPaint) {
        configureSeparatorPaint(separatorPaint);
        final int startX = this.getLeft();
        final int stopX = this.getRight();
        final int startY = getTop();
        canvas.drawLine(startX, startY, stopX, startY, separatorPaint);
    }
    
    protected void drawSectionFooterDivider(final Canvas canvas, final Paint separatorPaint) {
        configureSeparatorPaint(separatorPaint);
        final int startX = this.getLeft();
        final int stopX = this.getRight();
        final int startY = getBottom();
        canvas.drawLine(startX, startY, stopX, startY, separatorPaint);
    }
    
    // =====================================
    // MARK - Override addView / removeView
    // =====================================
    
    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        // From xml
        interceptAddView(child);
    }
    
    @Override
    public void removeView(View view) {
        // super.removeView(view);
        if (mCellStyle == Style.Custom) {
            mContentView.removeView(view);
        }
    }
    
    @Override
    public void removeViewInLayout(View view) {
        //super.removeViewInLayout(view);
        if (mCellStyle == Style.Custom) {
            mContentView.removeViewInLayout(view);
        }
    }
    
    @Override
    public void removeViewsInLayout(int start, int count) {
        //super.removeViewsInLayout(start, count);
        if (mCellStyle == Style.Custom) {
            mContentView.removeViewsInLayout(start, count);
        }
    }
    
    @Override
    public void removeViewAt(int index) {
        //super.removeViewAt(index);
        if (mCellStyle == Style.Custom) {
            mContentView.removeViewAt(index);
        }
    }
    
    @Override
    public void removeViews(int start, int count) {
        //super.removeViews(start, count);
        if (mCellStyle == Style.Custom) {
            mContentView.removeViews(start, count);
        }
    }
    
    @Override
    public void removeAllViews() {
        //super.removeAllViews();
        if (mCellStyle == Style.Custom) {
            mContentView.removeAllViews();
        }
    }
    
    @Override
    public void removeAllViewsInLayout() {
        //super.removeAllViewsInLayout();
        if (mCellStyle == Style.Custom) {
            mContentView.removeAllViewsInLayout();
        }
    }
    
    // ====================================================
    // MARK - Layout Manager
    // ====================================================
    
    private ImageView newAccessoryView() {
        ImageView imageView = new ImageView(getContext());
        imageView.setId(R.id.uitableview_cell_accessoryView);
        return imageView;
    }
    
    protected ImageView newStartImageView() {
        ImageView imageView = new ImageView(getContext());
        imageView.setId(R.id.uitableview_cell_imageView);
        return imageView;
    }
    
    protected TextView newTitleTextView() {
        TextView textView = new TextView(getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(R.style.UITableView_TextAppearance_Title);
        } else {
            textView.setTextAppearance(getContext(), R.style.UITableView_TextAppearance_Title);
        }
        textView.setId(R.id.uitableview_cell_titleTextView);
        textView.setText(R.string.uitableview_text_title);
        return textView;
    }
    
    protected TextView newDetailTextView() {
        TextView textView = new TextView(getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(R.style.UITableView_TextAppearance_Detail);
        } else {
            textView.setTextAppearance(getContext(), R.style.UITableView_TextAppearance_Detail);
        }
        textView.setId(R.id.uitableview_cell_valueTextView);
        textView.setText(R.string.uitableview_text_value);
        return textView;
    }
    
    protected void createLayoutForCustom() {
        // For subclass, you can create the custom layout in this method.
    }
    
    private void updateDefaultLayoutParams(){
        if (mCellStyle == Style.Custom){
            return;
        }
        RelativeLayout.LayoutParams titleParams = (RelativeLayout.LayoutParams) mTitleTextView.getLayoutParams();
        if (mImageView != null){
            titleParams.removeRule(RelativeLayout.ALIGN_PARENT_START);
            titleParams.addRule(RelativeLayout.END_OF, mImageView.getId());
        } else {
            titleParams.removeRule(RelativeLayout.END_OF);
            titleParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        }
    }
    
    // --------------------------------
    // | Title                        |
    // --------------------------------
    private void createLayoutForStyleDefault() {
        mTitleTextView = newTitleTextView();
        
        /* LayoutParams for Title */
        RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(-2, -2);
        titleParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        titleParams.addRule(RelativeLayout.CENTER_VERTICAL);
        
        mContentView.addView(mTitleTextView, titleParams);
    }
    
    // --------------------------------
    // | Title                  Value |
    // --------------------------------
    private void createLayoutForStyleValue1() {
        mTitleTextView = newTitleTextView();
        mDetailTextView = newDetailTextView();
    
        /* LayoutParams for Title */
        RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(-1, -2);
        titleParams.addRule(RelativeLayout.CENTER_VERTICAL);
        titleParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        titleParams.addRule(RelativeLayout.START_OF, mDetailTextView.getId());
        
        /* LayoutParams for Detail */
        RelativeLayout.LayoutParams detailParams = new RelativeLayout.LayoutParams(-2, -2);
        detailParams.addRule(RelativeLayout.CENTER_VERTICAL);
        detailParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        
        mContentView.addView(mTitleTextView, titleParams);
        mContentView.addView(mDetailTextView, detailParams);
    }
    
    // --------------------------------
    // | Title Value                  |
    // --------------------------------
    private void createLayoutForStyleValue2() {
        mTitleTextView = newTitleTextView();
        mDetailTextView = newDetailTextView();
        
        /* LayoutParams for Title */
        RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(-2, -2);
        titleParams.addRule(RelativeLayout.CENTER_VERTICAL);
        titleParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        
        /* LayoutParams for Detail */
        RelativeLayout.LayoutParams detailParams = new RelativeLayout.LayoutParams(-1, -2);
        detailParams.addRule(RelativeLayout.CENTER_VERTICAL);
        detailParams.addRule(RelativeLayout.END_OF, mTitleTextView.getId());
        detailParams.leftMargin = DimensionUtils.dp2px(getContext(), 16);
        
        mContentView.addView(mTitleTextView, titleParams);
        mContentView.addView(mDetailTextView, detailParams);
    }
    
    // --------------------------------
    // | Title                        |
    // | Subtitle                     |
    // --------------------------------
    private void createLayoutForStyleSubtitle() {
        mTitleTextView = newTitleTextView();
        mDetailTextView = newDetailTextView();
        
        /* LayoutParams for Title */
        RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(-1, -2);
        titleParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        
        /* LayoutParams for Detail */
        RelativeLayout.LayoutParams detailParams = new RelativeLayout.LayoutParams(-1, -2);
        detailParams.addRule(RelativeLayout.ALIGN_LEFT, mTitleTextView.getId());
        detailParams.addRule(RelativeLayout.BELOW, mTitleTextView.getId());
        
        mContentView.setGravity(Gravity.CENTER_VERTICAL);
        mContentView.addView(mTitleTextView, titleParams);
        mContentView.addView(mDetailTextView, detailParams);
    }
    
    // =====================================
    // MARK - Editing
    // =====================================
    
    private void ensureEditingControlLayout() {
        if (mEditingControlLayout == null) {
            mEditingControlLayout = new EditingControlLayout(getContext());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
            //params.addRule(RelativeLayout.ALIGN_PARENT_START);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            params.leftMargin = -mEditingControlLayout.getIntrinsicWidth();
            mForegroundLayout.addView(mEditingControlLayout, params);
        }
    }
    
    private void showEditingControlLayout(boolean showing) {
        if (mEditingControlLayout != null) {
            if (showing) {
                mEditingControlLayout.setVisibility(VISIBLE);
            } else {
                mEditingControlLayout.setVisibility(GONE);
            }
        }
    }
    
    public boolean isEditing() {
        return mEditing;
    }
    
    public void setEditing(boolean editing) {
        setEditing(editing, false);
    }
    
    public void setEditing(boolean editing, boolean animated) {
        // Log.d(TAG, "setEditing: oldEditing = " + mEditing + ", newEditing = " + editing);
        if (mEditing != editing) {
            mEditing = editing;
            Log.d(TAG, "setEditing: mEditing = " + mEditing);
            ensureEditingControlLayout();
            if (mEditing) {
                if (mCloseEditingAnimator != null && mCloseEditingAnimator.isRunning()) {
                    mCloseEditingAnimator.cancel();
                }
                // enableEditingControlLayout(true);
                showEditingControlLayout(true);
                transitionToEditingState(animated);
            } else {
                if (mOpenEditingAnimator != null && mOpenEditingAnimator.isRunning()) {
                    mOpenEditingAnimator.cancel();
                }
                closeEditingState(animated);
                mEditingStyle = EditingStyle.None;
                applySelectionStyle();
            }
        }
    }
    
    void setEditingStyle(@EditingStyle int editingStyle) {
        if (mEditingStyle != editingStyle) {
            mEditingStyle = editingStyle;
            Log.d(TAG, "setEditingStyle: mEditingStyle = " + mEditingStyle);
            if (mEditingControlLayout != null) {
                mEditingControlLayout.setupEditingStyle(mEditingStyle);
                mEditingControlLayout.setEditingButtonClickListener(getEditingButtonClickListener());
            }
        }
    }
    
    /* Editing for multiple selection */
    void setEditingForSelection() {
        if (mUserEditingSelectionDrawable == null) {
            mForegroundLayout.setBackgroundResource(R.drawable.uitableview_cell_editing_selectionstyle);
        } else {
            mForegroundLayout.setBackground(mUserEditingSelectionDrawable);
        }
        
        if (mEditingControlLayout != null) {
            mEditingControlLayout.setAllowSelection(true);
        }
    }
    
    // void enableEditingControlLayout(boolean enable) {
    //     if (enable) {
    //         if (mEditingControlLayout == null) {
    //             mEditingControlLayout = new EditingControlLayout(getContext());
    //             RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
    //             //params.addRule(RelativeLayout.ALIGN_PARENT_START);
    //             params.addRule(RelativeLayout.CENTER_VERTICAL);
    //             params.leftMargin = -mEditingControlLayout.getIntrinsicWidth();
    //             mForegroundLayout.addView(mEditingControlLayout, params);
    //         }
    //     } else {
    //         if (mEditingControlLayout != null) {
    //             removeFromParent(mEditingControlLayout);
    //         }
    //         mEditingControlLayout = null;
    //     }
    // }
    
    
    private OnClickListener getEditingButtonClickListener() {
        if (mEditingButtonClickListener == null) {
            mEditingButtonClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCellHelper.performCommitEditingStyle(UITableViewCell.this, mEditingStyle);
                }
            };
        }
        return mEditingButtonClickListener;
    }
    
    void transitionToEditingState(boolean animated) {
        // Offset needs to transition
        final float offset = mEditingControlLayout.getIntrinsicWidth() + mEditingIndentWidth;
        if (!animated) {
            translateEditingControlLayout(offset);
            return;
        }
        ValueAnimator valueAnimator = getOpenEditingAnimator();
        valueAnimator.setFloatValues(0, offset);
        valueAnimator.start();
    }
    
    void closeEditingState(boolean animated) {
        final float start = mEditingControlLayout.getTranslationX();
        final float end = 0;
        if (!animated) {
            translateEditingControlLayout(0);
            return;
        }
        ValueAnimator valueAnimator = getCloseEditingAnimator();
        valueAnimator.setFloatValues(start, end);
        valueAnimator.removeAllListeners();
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            boolean canceled = false;
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!canceled) {
                    showEditingControlLayout(false);
                }
            }
            
            @Override
            public void onAnimationCancel(Animator animation) {
                canceled = true;
            }
        });
        valueAnimator.start();
    }
    
    private ValueAnimator getOpenEditingAnimator() {
        if (mOpenEditingAnimator == null) {
            mOpenEditingAnimator = new ValueAnimator();
            mOpenEditingAnimator.setDuration(200);
            mOpenEditingAnimator.setInterpolator(new LinearInterpolator());
            mOpenEditingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    final float offset = (float) animation.getAnimatedValue();
                    translateEditingControlLayout(offset);
                }
            });
        }
        return mOpenEditingAnimator;
    }
    
    private ValueAnimator getCloseEditingAnimator() {
        if (mCloseEditingAnimator == null) {
            mCloseEditingAnimator = new ValueAnimator();
            mCloseEditingAnimator.setDuration(200);
            mCloseEditingAnimator.setInterpolator(new LinearInterpolator());
            mCloseEditingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    final float offset = (float) animation.getAnimatedValue();
                    translateEditingControlLayout(offset);
                }
            });
        }
        return mCloseEditingAnimator;
    }
    
    private void translateEditingControlLayout(float offset) {
        if (mAccessoryView != null) {
            mAccessoryView.setTranslationX(offset);
        }
        if (mContentView != null) {
            mContentView.setTranslationX(offset);
        }
        if (mEditingControlLayout != null) {
            mEditingControlLayout.setTranslationX(offset);
        }
        
        requestLayout(); // 更新separator
        
        // contentView被压缩，但是不会向右移动
        // mForegroundLayout.setPadding((int) offset,0,0,0);
    }
    
    // =====================================
    // MARK - Swiping
    // =====================================
    
    @Nullable
    SwipeActionLayout getLeadingSwipeLayout() {
        return mLeadingSwipeLayout;
    }
    
    @Nullable
    SwipeActionLayout getTrailingSwipeLayout() {
        return mTrailingSwipeLayout;
    }
    
    public boolean isSwipeActionEnabled() {
        return mSwipeActionEnabled;
    }
    
    public void setSwipeActionEnabled(boolean swipeActionEnabled) {
        if (mSwipeActionEnabled != swipeActionEnabled) {
            mSwipeActionEnabled = swipeActionEnabled;
            enableBackgroundLayout(mSwipeActionEnabled);
        }
    }
    
    public boolean isLeadingSwipeActionEnabled() {
        return mLeadingSwipeLayout != null;
    }
    
    public boolean isTrailingSwipeActionEnabled() {
        return mTrailingSwipeLayout != null;
    }
    
    private void enableBackgroundLayout(boolean enable) {
        if (enable) {
            if (mBackgroundLayout == null) {
                mBackgroundLayout = new RelativeLayout(getContext());
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1, -1);
                mBackgroundLayout.setVisibility(GONE);
                addViewToRoot(mBackgroundLayout, 0, params);
            }
        } else {
            if (mBackgroundLayout != null && mBackgroundLayout.getParent() == this) {
                removeFromParent(mBackgroundLayout);
                mBackgroundLayout = null;
            }
        }
    }
    
    private void enableTrailingSwipeActionLayout(boolean enable) {
        if (enable) {
            if (mTrailingSwipeLayout == null) {
                mTrailingSwipeLayout = new SwipeActionLayout(getContext(), SwipeLocation.Trailing);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -1);
                params.addRule(RelativeLayout.ALIGN_PARENT_END);
                mBackgroundLayout.addView(mTrailingSwipeLayout, params);
            }
        } else {
            if (mTrailingSwipeLayout != null && mTrailingSwipeLayout.getParent() == mBackgroundLayout) {
                removeFromParent(mTrailingSwipeLayout);
            }
            mTrailingSwipeLayout = null;
        }
    }
    
    private void enableLeadingSwipeActionLayout(boolean enable) {
        if (enable) {
            if (mLeadingSwipeLayout == null) {
                mLeadingSwipeLayout = new SwipeActionLayout(getContext(), SwipeLocation.Leading);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -1);
                params.addRule(RelativeLayout.ALIGN_PARENT_START);
                mBackgroundLayout.addView(mLeadingSwipeLayout, params);
            }
        } else {
            if (mLeadingSwipeLayout != null && mLeadingSwipeLayout.getParent() == mBackgroundLayout) {
                removeFromParent(mLeadingSwipeLayout);
            }
            mLeadingSwipeLayout = null;
        }
    }
    
    void configureLeadingSwipeActions(SwipeActionsConfiguration swipeActionsConfiguration) {
        enableLeadingSwipeActionLayout(swipeActionsConfiguration != null && swipeActionsConfiguration.hasActions());
        if (swipeActionsConfiguration != null && mLeadingSwipeLayout != null) {
            mLeadingSwipeLayout.setSwipeActionsConfiguration(swipeActionsConfiguration);
        }
    }
    
    void configureTrailingSwipeActions(SwipeActionsConfiguration swipeActionsConfiguration) {
        enableTrailingSwipeActionLayout(swipeActionsConfiguration != null && swipeActionsConfiguration.hasActions());
        if (swipeActionsConfiguration != null && mTrailingSwipeLayout != null) {
            mTrailingSwipeLayout.setSwipeActionsConfiguration(swipeActionsConfiguration);
        }
    }
    
    void willStartSwiping(@SwipeLocation int swipeLocation) {
        switch (swipeLocation) {
            case SwipeLocation.Leading: {
                showSwipeActionLayout(swipeLocation);
                mLeadingSwipeLayout.willStartSwiping(this.getMeasuredWidth());
                break;
            }
            case SwipeLocation.Trailing: {
                showSwipeActionLayout(swipeLocation);
                mTrailingSwipeLayout.willStartSwiping(this.getMeasuredWidth());
                break;
            }
            case SwipeLocation.None:
                break;
        }
    }
    
    void translateForegroundLayout(float offset) {
        mForegroundLayout.setTranslationX(offset);
        if (offset > 0) {
            mLeadingSwipeLayout.onSwiped(offset);
        } else if (offset < 0) {
            mTrailingSwipeLayout.onSwiped(offset);
        } else {
            if (mLeadingSwipeLayout != null && mLeadingSwipeLayout.getVisibility() == VISIBLE) {
                mLeadingSwipeLayout.onSwiped(0);
            }
            if (mTrailingSwipeLayout != null && mTrailingSwipeLayout.getVisibility() == VISIBLE) {
                mTrailingSwipeLayout.onSwiped(0);
            }
        }
    }
    
    void showSwipeActionLayout(@SwipeLocation int swipeLocation) {
        if (mBackgroundLayout.getVisibility() != VISIBLE) {
            mBackgroundLayout.setVisibility(VISIBLE);
        }
        if (swipeLocation == SwipeLocation.Leading) {
            mLeadingSwipeLayout.setVisibility(VISIBLE);
            if (mTrailingSwipeLayout != null) {
                mTrailingSwipeLayout.setVisibility(GONE);
            }
        } else if (swipeLocation == SwipeLocation.Trailing) {
            mTrailingSwipeLayout.setVisibility(VISIBLE);
            if (mLeadingSwipeLayout != null) {
                mLeadingSwipeLayout.setVisibility(GONE);
            }
        }
    }
    
    void dismissSwipeActionLayout() {
        mBackgroundLayout.setVisibility(GONE);
    }
    
    /* 动画放在Cell中是为了将每个cell区分开，在点击新的Cell时，可以对旧的Cell进行操作 */
    ValueAnimator transitionToNormalState(@Nullable Animator.AnimatorListener animatorListener) {
        final float startOffset = mForegroundLayout.getTranslationX();
        final float endOffset = 0;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(startOffset, endOffset);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(200);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                translateForegroundLayout((Float) animation.getAnimatedValue());
            }
        });
        if (animatorListener != null) {
            valueAnimator.addListener(animatorListener);
        }
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                dismissSwipeActionLayout();
            }
        });
        valueAnimator.start();
        return valueAnimator;
    }
    
    ValueAnimator transitionToSwipeState(final float endOffset, @Nullable Animator.AnimatorListener animatorListener) {
        final float startOffset = mForegroundLayout.getTranslationX();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(startOffset, endOffset);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(200);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                translateForegroundLayout((Float) animation.getAnimatedValue());
            }
        });
        if (animatorListener != null) {
            valueAnimator.addListener(animatorListener);
        }
        valueAnimator.start();
        return valueAnimator;
    }
    
    
    /**
     * These methods can be used by subclasses to animate additional changes to the cell when the cell is changing state
     *
     * @param oldState old cell state
     * @param newState new cell state
     */
    protected void willTransitionToState(@StateMask int oldState, @StateMask int newState) {
        // TODO: 2018/5/6  not implemented
    }
    
    protected void didTransitionToState(@StateMask int oldState, @StateMask int newState) {
    
    }
    
    // =====================================
    // MARK - Selected
    // =====================================
    
    @Override
    public void setSelected(boolean selected) {
        final boolean oldSelection = isSelected();
        // Log.d(TAG, "setSelected: old selection = " + oldSelection);
        super.setSelected(selected);
        if (oldSelection != selected) {
            if (mEditing) {
                onSelectionChangedDuringEditing(selected);
            } else {
                onSelectionChanged(selected);
            }
        }
    }
    
    protected void onSelectionChangedDuringEditing(boolean selected) {
        mEditingControlLayout.setEditingSelection(selected);
    }
    
    protected void onSelectionChanged(boolean selected) {
        if (mCellStyle == Style.DefaultSelectable){
            if (selected){
                setAccessoryType(AccessoryType.CheckMark);
            } else {
                setAccessoryType(AccessoryType.None);
            }
        }
    }
    
    // ==========================
    // MARK - Set CellModel
    // ==========================
    
    public T getModel() {
        return mModel;
    }
    
    @SuppressWarnings("unchecked")
    public void setModel(UITableViewCellModel model) {
        this.setModelInternal((T) model);
    }
    
    protected void setModelInternal(T model) {
        mModel = model;
        if (mModel != null) {
            setTitleText(mModel.getTitleText());
            setDetailText(mModel.getDetailText());
            
            if (model.isImageDrawableSet()) {
                setImageDrawable(model.getImageDrawable(getContext()));
            }
            
            if (mModel.accessoryType != UNDEFINED) {
                setAccessoryType(mModel.accessoryType);
            }
            
            if (mModel.selectionStyle != UNDEFINED) {
                setSelectionStyle(mModel.selectionStyle);
            }
        }
        
        // Subclass should override this method
    }
    
    // =====================================
    // MARK - Convenience
    // =====================================
    
    protected int getDimenPixel(@DimenRes int dimen) {
        return getResources().getDimensionPixelOffset(dimen);
    }
    
    protected Drawable internalGetDrawable(int resId) {
        return ContextCompat.getDrawable(getContext(), resId);
    }
    
    protected void removeFromParent(View child) {
        if (child != null && child.getParent() != null) {
            final ViewGroup parent = (ViewGroup) child.getParent();
            parent.removeView(child);
        }
    }
    
    // protected boolean isViewDisabled(View view) {
    //     return view == null || view.getParent() == null || view.getVisibility() == GONE;
    // }
    
    protected boolean isViewEnabled(View view){
        return view != null && view.getParent() != null && view.getVisibility() != GONE;
    }
    
}
