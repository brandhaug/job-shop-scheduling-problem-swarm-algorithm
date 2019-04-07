package gantt;

import javafx.collections.FXCollections;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import jssp.Machine;
import jssp.OperationTimeSlot;

import java.util.ArrayList;
import java.util.List;

public class JSSPGanttChart {
    private List<String> machineLabels = new ArrayList<>();
    private List<Machine> machines = new ArrayList<>();
    private List<XYChart.Series> series = new ArrayList<>();
    private final NumberAxis xAxis = new NumberAxis();
    private final CategoryAxis yAxis = new CategoryAxis();

    public JSSPGanttChart(List<Machine> machines) {
        this.machines = machines;

        for (Machine machine : machines) {
            this.machineLabels.add("Machine " + machine.id());
            this.series.add(new XYChart.Series());
        }

        xAxis.setLabel("");
        xAxis.setTickLabelFill(Color.CHOCOLATE);
        xAxis.setMinorTickCount(4);

        yAxis.setLabel("");
        yAxis.setTickLabelFill(Color.CHOCOLATE);
        yAxis.setTickLabelGap(10);
        yAxis.setCategories(FXCollections.<String>observableArrayList(machineLabels));
    }

    public GanttChart<Number, String> initializeGanttChart(List<OperationTimeSlot> schedule) {
        final GanttChart<Number, String> chart = new GanttChart<Number, String>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setBlockHeight(50);

        String[] classes = new String[]{
                "status-1",
                "status-2",
                "status-3",
                "status-4",
                "status-5",
                "status-6",
                "status-7",
                "status-8",
                "status-9",
                "status-10",
                "status-11",
                "status-12",
                "status-13",
                "status-14",
                "status-15",
                "status-16",
                "status-17",
                "status-18",
                "status-19",
                "status-20",
                "status-21"
        };

        chart.getData().clear();

        for (int i = 0; i < machineLabels.size(); i++) {
            for (OperationTimeSlot operationTimeSlot : schedule) {
                if (operationTimeSlot.operation().machineId() == machines.get(i).id()) {
                    series.get(i).getData().add(new XYChart.Data(operationTimeSlot.start(), machineLabels.get(i), new GanttChart.ExtraData(operationTimeSlot.end() - operationTimeSlot.start(), classes[operationTimeSlot.operation().jobId()])));
                }
            }

            chart.getData().addAll(series.get(i));
        }

        return chart;
    }
}
