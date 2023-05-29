package fi.septicuss.tooltips.object.preset.condition.composite;

import fi.septicuss.tooltips.object.preset.condition.composite.CompositeCondition.Operator;
import fi.septicuss.tooltips.object.preset.condition.parser.ParsedCondition;

public class CompositeConditionBuilder {

	private CompositeCondition root;

	/*-*
	// meow AND meow
	//				 null
	//				/    \
	//			   /  AND \
	//			 meow     meow
	//
	
	// meow AND meow OR meow OR meow
	// 			    null
	//			   /    \
	//            /  AND \
	//			meow    null
	//				   /    \
	//                /  OR  \
	//             meow     null 
	//                     /    \
	//                    /  OR  \
	//                  meow     meow
	//
	
	// (meow AND meow) OR (meow OR meow)
	//				null
	//		       /    \
	//            /  OR  \
	//         null       null
	//         /  \       /  \
	//        / AND\     / OR \
	//       meow  meow meow meow
	//
	**/
	public CompositeConditionBuilder() {
		this.root = new CompositeCondition(null);
	}

	public CompositeConditionBuilder with(ParsedCondition condition) {
		var left = new CompositeCondition(condition);
		root.setLeft(left);
		return this;
	}

	public CompositeConditionBuilder with(CompositeCondition composite) {
		root.setLeft(composite);
		return this;
	}

	public CompositeConditionBuilder append(Operator operator, ParsedCondition condition) {
		root.setOperator(operator);
		root.setRight(new CompositeCondition(condition));
		return this;
	}

	public CompositeConditionBuilder append(Operator operator, CompositeCondition composite) {
		root.setOperator(operator);
		root.setRight(composite);
		return this;
	}

	public CompositeCondition build() {
		return root;
	}

}
