package gantt;

import javafx.collections.FXCollections;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import jssp.Machine;
import jssp.OperationTimeSlot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JSSPGanttChart {
    private List<String> machineLabels = new ArrayList<>();
    private List<Machine> machines;
    private List<XYChart.Series> series = new ArrayList<>();
    private final NumberAxis xAxis = new NumberAxis();
    private final CategoryAxis yAxis = new CategoryAxis();

    public JSSPGanttChart(List<Machine> machines) {
        this.machines = machines;

        for (Machine machine : machines) {
            this.machineLabels.add("M" + (machine.id() + 1));
            this.series.add(new XYChart.Series());
        }

        Collections.reverse(machineLabels);

        xAxis.setLabel("Time");
        yAxis.setLabel("Machines");
        yAxis.setCategories(FXCollections.observableArrayList(machineLabels));
    }

    public GanttChart<Number, String> initializeGanttChart(List<OperationTimeSlot> schedule) {
        final GanttChart<Number, String> chart = new GanttChart(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setBlockHeight(50);

        String[] colorClasses = new String[]{
                "c-1", "c-2", "c-3", "c-4", "c-5", "c-6", "c-7", "c-8", "c-9", "c-10", "c-11", "c-12", "c-13", "c-14",
                "c-15", "c-16", "c-17", "c-18", "c-19", "c-20", "c-21"
        };

        chart.getData().clear();

        for (int i = 0; i < machineLabels.size(); i++) {
            for (OperationTimeSlot operationTimeSlot : schedule) {
                if (operationTimeSlot.operation().machineId() == machines.get(i).id()) {
                    series.get(i).getData().add(new XYChart.Data(operationTimeSlot.start(), machineLabels.get(i), new GanttChart.ExtraData(operationTimeSlot.end() - operationTimeSlot.start(), colorClasses[operationTimeSlot.operation().jobId()])));
                }
            }

            chart.getData().addAll(series.get(i));
        }

        return chart;
    }
}
