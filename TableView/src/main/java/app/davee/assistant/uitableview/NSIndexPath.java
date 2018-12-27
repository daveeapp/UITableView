package app.davee.assistant.uitableview;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * 仿ios的NSIndexPath，定义两个参数（section，row），标记出cell在table中的位置
 */
public class NSIndexPath {
    
    public static final int COMPARE_BEFORE = -1;
    public static final int COMPARE_EQUAL = 0;
    public static final int COMPARE_AFTER = 1;
    
    public int section;
    public int row;
    
    public NSIndexPath(int section, int row) {
        this.section = section;
        this.row = row;
    }
    
    public void set(NSIndexPath indexPath){
        if (indexPath != null){
            set(indexPath.section, indexPath.row);
        }
    }
    
    public void set(int section, int row){
        this.section = section;
        this.row = row;
    }
    
    public boolean equals(NSIndexPath indexPath) {
        return indexPath != null && indexPath.section == this.section && indexPath.row == this.row;
    }
    
    public int compare(@NonNull NSIndexPath indexPath){
        if (this.section > indexPath.section){
            return COMPARE_AFTER;
        } else if (this.section == indexPath.section){
            return Integer.compare(this.row, indexPath.row);
        } else {
            return COMPARE_BEFORE;
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        
        NSIndexPath indexPath = (NSIndexPath) obj;
        return indexPath.section == this.section && indexPath.row == this.row;
    }
    
    @SuppressWarnings("DefaultLocale")
    @Override
    public String toString() {
        return String.format("[section = %d, row = %d]", section, row);
    }
    
    /// smaller to bigger
    public static void sortIndexPathArray(@NonNull ArrayList<NSIndexPath> indexPaths){
        if (!indexPaths.isEmpty()){
            Collections.sort(indexPaths, new IndexPathComparator());
        }
    }
    
    /// bigger to smaller
    public static void sortIndexPathArrayReversed(@NonNull ArrayList<NSIndexPath> indexPaths){
        if (!indexPaths.isEmpty()){
            Collections.sort(indexPaths, new IndexPathReversedComparator());
        }
    }
    
    public static class IndexPathComparator implements Comparator<NSIndexPath>{
    
        @Override
        public int compare(NSIndexPath o1, NSIndexPath o2) {
            return o1.compare(o2);
        }
    }
    
    public static class IndexPathReversedComparator implements Comparator<NSIndexPath>{
        
        @Override
        public int compare(NSIndexPath o1, NSIndexPath o2) {
            return o2.compare(o1);
        }
    }
}
