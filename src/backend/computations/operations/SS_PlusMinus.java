/**
 * TODO: test the toLatex
 */
package backend.computations.operations;

import java.util.ArrayList;
import java.util.List;

import backend.blocks.Countable;
import backend.blocks.Countable.DisplayType;
import backend.blocks.Op;
import backend.blocks.Scalar;
import backend.computations.infrastructure.Computable;
import backend.computations.infrastructure.Solution;
import backend.computations.infrastructure.Step;

/** 
 * Scalar addition and subtraction operations
 * 
 * @author baebi
 */
public class SS_PlusMinus extends Computable {
	private Solution _solution;
	private String _operatorStep;
	
	
	/** 
	 * Computes the solution to a scalar addition or subtraction operation. Expects non-null inputs
	 * 
	 * @param a the first number
	 * @param b the second number
	 * @param isPlus true iff this is an addition operation. false iff this is a subtraction operation
	 */
	public SS_PlusMinus(Scalar a, Scalar b, boolean isPlus){
		List<Countable> args = new ArrayList<>();
		args.add(a);
		args.add(b);
		DisplayType answerDisplayType = resolveDisplayType(args);
		a.setDisplayType(answerDisplayType);
		b.setDisplayType(answerDisplayType);
		
		Double aVal = a.getValue();
		Double bVal = b.getValue();
		
		
		String operatorStep; Double answer;
		if (isPlus){
			operatorStep = a.getDisplayValue() + " + " + b.getDisplayValue();
			answer = aVal + bVal;
		}else{
			operatorStep = a.getDisplayValue() + " - " + b.getDisplayValue();
			answer = aVal - bVal;
		}
		
		Scalar opStep = new Scalar(answer,DisplayType.CUSTOM);
		opStep.setCustomDisplayValue(operatorStep);
		Step step1 = new Step(opStep);
		Scalar answerStep = new Scalar(answer,answerDisplayType);
		Step step2 = new Step(answerStep);
		List<Step> steps = new ArrayList<>();
		steps.add(step1);
		steps.add(step2);
		if (isPlus){
			_solution = new Solution(Op.SS_PLUS,args,answerStep,steps);
		}else{
			_solution = new Solution(Op.SS_MINUS,args,answerStep,steps);
		}
	}
	
	
	@Override
	public Solution getSolution() {
		return _solution;
	}


	@Override
	public List<String> toLatex() {
		List<String> toReturn = new ArrayList<>();
		StringBuilder b = new StringBuilder();
		b.append("$");
		b.append(_operatorStep);
		b.append(" = ");
		b.append(((Scalar)_solution.getAnswer()).getDisplayValue());
		b.append("$");
		toReturn.add(b.toString());
		return toReturn;
	}

}
