package com.example.poussapoussi.databinding;
import com.example.poussapoussi.R;
import com.example.poussapoussi.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class FragmentGridBindingImpl extends FragmentGridBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.mainMenu, 1);
        sViewsWithIds.put(R.id.orangeTurn, 2);
        sViewsWithIds.put(R.id.blueTurn, 3);
        sViewsWithIds.put(R.id.scoreBoard, 4);
        sViewsWithIds.put(R.id.gridContainer, 5);
        sViewsWithIds.put(R.id.borderGridLayout, 6);
        sViewsWithIds.put(R.id.tokenGridLayout, 7);
        sViewsWithIds.put(R.id.squareLayout, 8);
        sViewsWithIds.put(R.id.winnerBackground, 9);
        sViewsWithIds.put(R.id.winnerTextView, 10);
    }
    // views
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public FragmentGridBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 11, sIncludes, sViewsWithIds));
    }
    private FragmentGridBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.TextView) bindings[3]
            , (android.widget.GridLayout) bindings[6]
            , (android.widget.FrameLayout) bindings[5]
            , (android.widget.Button) bindings[1]
            , (android.widget.TextView) bindings[2]
            , (android.widget.TextView) bindings[4]
            , (android.widget.FrameLayout) bindings[8]
            , (android.widget.GridLayout) bindings[7]
            , (androidx.constraintlayout.widget.ConstraintLayout) bindings[0]
            , (android.widget.FrameLayout) bindings[9]
            , (android.widget.TextView) bindings[10]
            );
        this.wholeScreen.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x1L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
            return variableSet;
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        // batch finished
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): null
    flag mapping end*/
    //end
}