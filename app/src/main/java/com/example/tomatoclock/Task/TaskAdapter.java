package com.example.tomatoclock.Task;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tomatoclock.R;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private List<Task> taskList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView taskInfor;
        TextView taskDdl;
        TaskAdapter taskAdapter;//
        int task_position;//

        public ViewHolder(View view, TaskAdapter taskAdapter){
            super(view);
            this.taskAdapter = taskAdapter;//
            taskInfor = (TextView) view.findViewById(R.id.task_infor);
            taskDdl = (TextView) view.findViewById(R.id.task_ddl);

            taskInfor.setSingleLine(true);

            initClickListener();
        }
        private void initClickListener(){
            taskInfor.setOnClickListener(new View.OnClickListener() {
                boolean isExpand = false;
                @Override
                public void onClick(View view) {
                    taskInfor.setSingleLine(isExpand);
                    isExpand = !isExpand;
                }
            });
            taskInfor.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //taskAdapter.taskList.remove(task_position);//
                    return true;
                }
            });
        }
    }

    public TaskAdapter(List<Task> taskList){
        this.taskList = taskList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        ViewHolder holder = new ViewHolder(view, this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskInfor.setText(task.getInfor());
        holder.taskDdl.setText(task.getDdl());
        holder.task_position = position;
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

}
