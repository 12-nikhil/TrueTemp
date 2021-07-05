package softwise.mechatronics.truBlueMonitor.viewHolders;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.softwise.trumonitor.R;


public class AssetsViewHolder extends RecyclerView.ViewHolder {

   public RelativeLayout layout;
   public TextView txtAssetName;

    public AssetsViewHolder(View view) {
        super(view);
        layout = view.findViewById(R.id.list_item);
        txtAssetName = view.findViewById(R.id.txt_asset_name);
    }
}
