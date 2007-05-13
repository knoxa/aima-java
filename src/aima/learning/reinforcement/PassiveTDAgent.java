package aima.learning.reinforcement;

import java.util.Hashtable;

import aima.probability.decision.MDP;
import aima.probability.decision.MDPPerception;
import aima.probability.decision.MDPPolicy;
import aima.probability.decision.MDPUtilityFunction;
import aima.util.FrequencyCounter;

public class PassiveTDAgent <STATE_TYPE, ACTION_TYPE> extends
MDPAgent<STATE_TYPE, ACTION_TYPE>{
	
	private MDPPolicy<STATE_TYPE, ACTION_TYPE> policy;
	private MDPUtilityFunction<STATE_TYPE> utilityFunction;
	//private Hashtable<STATE_TYPE,Double> stateCount;
	private FrequencyCounter<STATE_TYPE> stateCount;
	
	private Double previousReward;
	
	
	

	public PassiveTDAgent(MDP<STATE_TYPE, ACTION_TYPE> mdp,
			MDPPolicy<STATE_TYPE, ACTION_TYPE> policy) {
		super(mdp.emptyMdp());
		this.policy = policy;
		this.utilityFunction = new MDPUtilityFunction<STATE_TYPE>();
		this.stateCount = new  FrequencyCounter<STATE_TYPE>();
	}
	
	public ACTION_TYPE decideAction(MDPPerception<STATE_TYPE> perception) {
		
		if (!(utilityFunction.hasUtilityFor(perception.getState()))) { //if perceptionState is new
			utilityFunction.setUtility(perception.getState(), perception
					.getReward());
			mdp.setReward(perception.getState(), perception.getReward());
		}
		if (!(previousState == null)) {
			stateCount.incrementFor(previousState);
			utilityFunction = updateUtilityFunction(1.0, 0.1);
		}
		
		
		if (mdp.isTerminalState(currentState)) {
			previousState = null;
			previousAction =null;
			previousReward = null;
		}else{
			previousState = currentState;
			previousAction = policy.getAction(currentState);
			previousReward = currentReward;
		}
		return previousAction;
	}

	private MDPUtilityFunction<STATE_TYPE> updateUtilityFunction(double gamma,double alpha) {
		MDPUtilityFunction<STATE_TYPE> uf = utilityFunction.copy();
		double u_s =  utilityFunction.getUtility(previousState);
		double gammaUtilDIff =  ((gamma * utilityFunction.getUtility(currentState)) - utilityFunction.getUtility(previousState));
		//double alphaTerm =  alpha * stateCount.getCount(previousState)*(previousReward + gammaUtilDIff);
		double alphaTerm = stateCount.probabilityOf(previousState)*(previousReward + gammaUtilDIff);
		uf.setUtility(previousState, u_s+alphaTerm);
		return uf;
	}
	
	public MDPUtilityFunction<STATE_TYPE> getUtilityFunction() {

		return utilityFunction;
	}
}