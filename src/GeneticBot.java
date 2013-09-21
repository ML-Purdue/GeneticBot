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
        ArrayList<Double> shouldFireEquationWeights = new ArrayList<Double>();
        
        ArrayList<Double> inputs = new ArrayList<Double>();
        
        inputs.add((double) 1); // constant factor
        turnGunEquationWeights.add(randomRange(-1, 1));
        fireEquationWeights.add(randomRange(-1, 1));
        shouldFireEquationWeights.add(randomRange(-1, 1));
        
        inputs.add((double) 0); // time since last scan
        turnGunEquationWeights.add(randomRange(-1, 1));
        fireEquationWeights.add(randomRange(-1, 1));
        shouldFireEquationWeights.add(randomRange(-1, 1));
        
        while (true) {
            if (lastScanEvent != null) {
                inputs.set(1, (double) (getTime() - lastScanEvent.getTime()));
            }
            double turnGun = evaluateEquation(turnGunEquationWeights, inputs);
            boolean shouldFire = evaluateEquation(shouldFireEquationWeights, inputs) > 0;
            double fire = evaluateEquation(fireEquationWeights, inputs);
            setTurnGunRight(turnGun); // schedule the gun to turn
            if (shouldFire) {
            	setFire(fire); // schedule to fire a bullet
            }
            System.out.println("Turn " + getTime());
            debug("turnGun", turnGun, turnGunEquationWeights, inputs);
            debug("shouldFire", shouldFire ? 1 : 0, shouldFireEquationWeights, inputs);
            debug("fire", fire, fireEquationWeights, inputs);
            
            execute(); // give Robocode control for one game tick
        }
    }

    private void debug(String name, double value, ArrayList<Double> weights, ArrayList<Double> inputs) {
    	if (weights.size() == 0) {
    		return;
    	}
    	
    	String expressionFormatter = " %.2f*%.2f";
    	String rightHandSide = String.format("%.2f*%.2f", weights.get(0), inputs.get(0));
    	for (int i = 1; i < weights.size(); i++) {
    		rightHandSide += " + " + String.format(expressionFormatter, weights.get(i), inputs.get(i));
    	}
    	
    	System.out.printf("%s %.2f = %s\n", name, value, rightHandSide);
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
    
    private double randomRange(double a, double b) {
    	return a + Math.random() * (b-a);
    }
}
