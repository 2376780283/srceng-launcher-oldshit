package zzh.util.log;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {
  private final List<String> logLines;

  public LogAdapter(List<String> logLines) {
    this.logLines = logLines;
  }

  @NonNull
  @Override
  public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext())
            .inflate(android.R.layout.simple_list_item_1, parent, false);
    return new LogViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
    holder.bind(logLines.get(position));
  }

  @Override
  public int getItemCount() {
    return logLines.size();
  }

  public void addLogLines(List<String> newLines) {
    int startPosition = logLines.size();
    logLines.addAll(newLines);
    notifyItemRangeInserted(startPosition, newLines.size());
  }

  static class LogViewHolder extends RecyclerView.ViewHolder {
    private final TextView textView;

    public LogViewHolder(@NonNull View itemView) {
      super(itemView);
      textView = itemView.findViewById(android.R.id.text1);
    }

    public void bind(String logLine) {
      textView.setText(logLine);
    }
  }
}
