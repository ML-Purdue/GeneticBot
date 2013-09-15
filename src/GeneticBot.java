import java.util.ArrayList;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class GeneticBot extends AdvancedRobot {
    ScannedRobotEvent lastScanEvent = null;

    public GeneticBot() {
        super();
    }

    // TODO implement repeated mutation of weights after rounds and keep the best set
    // TODO add more types of input
    // TODO try different equations besides linear (e.g. quadratic, cubic, sine waves, tree-based construction of arbitrary equations)
    public void run() {
        ArrayList<Double> turnGunEquationWeights = new ArrayList<Double>();
        ArrayList<Double> fireEquationWeights = new ArrayList<Double>();

        ArrayList<Double> inputs = new ArrayList<Double>();

        inputs.add((double) 1); // constant factor
        turnGunEquationWeights.add(Math.random());
        fireEquationWeights.add(Math.random());

        inputs.add((double) 0); // time since last scan
        turnGunEquationWeights.add(Math.random());
        fireEquationWeights.add(Math.random());

        while (true) {
            if (lastScanEvent != null) {
                inputs.set(1, (double) (getTime() - lastScanEvent.getTime()));
            }
            double turnGun = evaluateEquation(turnGunEquationWeights, inputs);
            double fire = evaluateEquation(fireEquationWeights, inputs);
            setTurnGunRight(turnGun); // schedule the gun to turn
            setFire(fire); // schedule to fire a bullet
            System.out.printf("turnGun %.2f = %.2f*%.2f + %.2f*%.2f, fire %.2f = %.2f*%.2f + %.2f*%.2f\n",
                    turnGun,
                    turnGunEquationWeights.get(0),
                    inputs.get(0),
                    turnGunEquationWeights.get(1),
                    inputs.get(1),
                    fire,
                    fireEquationWeights.get(0),
                    inputs.get(0),
                    fireEquationWeights.get(1),
                    inputs.get(1));
            execute(); // give Robocode control for one game tick
        }
    }

    // Evaluate a linear equation in the form of [value = weight1 * input1 + weight2* input2 + w3 * i3 + ...]
    private double evaluateEquation(ArrayList<Double> weights, ArrayList<Double> inputs) {
        double value = 0;
        for (int i = 0; i < weights.size(); i++) {
            value += weights.get(i) * inputs.get(i);
        }
        return value;
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        lastScanEvent = e; // save the last scanned robot event for input into equations
    }
}
