package zzh.source.srceng.util.command;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
import zzh.source.srceng.R;

public class SuggestionAdapter extends ArrayAdapter<String> {
  private Context context;
  private List<String> items;

  public SuggestionAdapter(Context context, List<String> items) {
    super(context, 0, items);
    this.context = context;
    this.items = items;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = LayoutInflater.from(context).inflate(R.layout.item_cmdline_f, parent, false);
    }
    TextView textView = convertView.findViewById(R.id.text_view_item);
    String item = getItem(position);
    textView.setText(item);
    return convertView;
  }
}
