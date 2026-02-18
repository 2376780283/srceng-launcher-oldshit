package zzh.source.srceng.feature.motd;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
public class motdofitem {
    private int imageResId;
    private String primaryText;
    private String secondaryText;

    public motdofitem(int imageResId, String primaryText, String secondaryText) {
        this.imageResId = imageResId;
        this.primaryText = primaryText;
        this.secondaryText = secondaryText;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getPrimaryText() {
        return primaryText;
    }

    public String getSecondaryText() {
        return secondaryText;
    }
}