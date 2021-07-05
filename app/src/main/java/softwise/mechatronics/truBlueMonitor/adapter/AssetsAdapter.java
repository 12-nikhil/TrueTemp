package softwise.mechatronics.truBlueMonitor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.recyclerview.widget.RecyclerView;

import com.softwise.trumonitor.R;
import softwise.mechatronics.truBlueMonitor.models.AssetAndSensorInfo;
import softwise.mechatronics.truBlueMonitor.viewHolders.AssetsViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AssetsAdapter extends RecyclerView.Adapter<AssetsViewHolder> implements Filterable {

    OnAssetsSelectListeners mOnAssetsSelectListeners;
    List<AssetAndSensorInfo> mAssetAndSensorInfo = new ArrayList<>();
    List<AssetAndSensorInfo> mAssetAndSensorInfoOriginal = new ArrayList<>();
    List<AssetAndSensorInfo> mAssetAndSensorInfoFilter = new ArrayList<>();
    Context mContext;

    public AssetsAdapter(Context context, List<AssetAndSensorInfo> assetAndSensorInfoList, OnAssetsSelectListeners onAssetsSelectListeners) {
        this.mOnAssetsSelectListeners = onAssetsSelectListeners;
        this.mAssetAndSensorInfoOriginal = assetAndSensorInfoList;
        this.mAssetAndSensorInfoFilter = assetAndSensorInfoList;
        this.mAssetAndSensorInfo = assetAndSensorInfoList;
        this.mContext = context;
    }

    @NotNull
    @Override
    public AssetsViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new AssetsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.assets_item_unit, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull AssetsViewHolder holder, int position) {
        AssetAndSensorInfo assetAndSensorInfo = mAssetAndSensorInfo.get(position);
        holder.txtAssetName.setText(assetAndSensorInfo.getAssetName());
        holder.layout.setOnClickListener(v -> {
            mOnAssetsSelectListeners.assetsSelect(assetAndSensorInfo);
        });

    }

    @Override
    public int getItemCount() {
        return mAssetAndSensorInfo.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mAssetAndSensorInfoFilter = mAssetAndSensorInfoOriginal;
                } else {
                    List<AssetAndSensorInfo> filteredList = new ArrayList<>();
                    for (AssetAndSensorInfo assetAndSensorInfo : mAssetAndSensorInfo) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (assetAndSensorInfo.getAssetName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(assetAndSensorInfo);
                        }
                    }

                    mAssetAndSensorInfoFilter = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mAssetAndSensorInfoFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mAssetAndSensorInfo.clear();
                mAssetAndSensorInfo = (ArrayList<AssetAndSensorInfo>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
    public void updateList(List<AssetAndSensorInfo> assetAndSensorInfoList)
    {
        mAssetAndSensorInfo.clear();
        mAssetAndSensorInfo.addAll(assetAndSensorInfoList);
        notifyDataSetChanged();
    }

    public interface OnAssetsSelectListeners {
        void assetsSelect(AssetAndSensorInfo assetAndSensorInfo);
    }
}
