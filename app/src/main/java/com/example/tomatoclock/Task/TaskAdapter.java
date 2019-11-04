package com.example.tomatoclock.Task;

import android.app.AppOpsManager;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tomatoclock.R;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private List<Task> taskList;
    private TasksActivity tasksActivity;


    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView taskInfor;
        TextView taskDdl;
        TaskAdapter taskAdapter;
        Task task;
        public ViewHolder(View view, TaskAdapter ta){
            super(view);
            taskInfor = (TextView) view.findViewById(R.id.task_infor);
            taskDdl = (TextView) view.findViewById(R.id.task_ddl);
            taskAdapter = ta;
            taskInfor.setSingleLine(true);
            taskInfor.setEllipsize(TextUtils.TruncateAt.END);
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
                public boolean onLongClick(final View view) {
                    PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                    popupMenu.getMenuInflater().inflate(R.menu.task_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int pos;
                            switch (item.getItemId()) {
                                case R.id.taskEditItem:
                                    pos = taskAdapter.taskList.indexOf(task);
                                    taskAdapter.tasksActivity.editTask(pos, task.getInfor());
                                    break;
                                case R.id.taskDeleteItem:
                                    pos = taskAdapter.taskList.indexOf(task);
                                    taskAdapter.removeTask(pos);
                                    break;
                                case R.id.taskBeginItem:
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                    return true;
                }
            });
            taskDdl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            int temp = monthOfYear + 1;
                            String monthStr = temp < 10 ? "0" + temp : "" + temp;
                            temp = dayOfMonth;
                            String dayStr = temp < 10 ? "0" + temp : "" + temp;
                            task.updateDdl(year + "-" + monthStr + "-" + dayStr);
                            taskAdapter.notifyItemChanged(taskAdapter.taskList.indexOf(task));
                        }
                    }, 2019, 11, 1).show();
                }
            });
        }
    }

    public TaskAdapter(TasksActivity tasksActivity, List<Task> taskList){
        this.taskList = taskList;
        this.tasksActivity = tasksActivity;
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
        holder.task = task;
        holder.taskInfor.setText(task.getInfor());
        holder.taskDdl.setText(task.getDdl());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    private void removeTask(int taskPosition) {
        taskList.remove(taskPosition);
        notifyItemRemoved(taskPosition);
    }
    public void addData(Task task) {
        taskList.add(task);
        notifyItemInserted(taskList.indexOf(task));

    }

}
