package fi.septicuss.tooltips.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Credit to:
 * https://github.com/naivesound/expr-java
 */
public interface Expr {
	float eval();

	enum Op {
		UNARY_MINUS,
		UNARY_LOGICAL_NOT,
		UNARY_BITWISE_NOT,

		POWER,
		MULTIPLY,
		DIVIDE,
		REMAINDER,

		PLUS,
		MINUS,

		SHL,
		SHR,

		LT,
		LE,
		GT,
		GE,
		EQ,
		NE,

		BITWISE_AND,
		BITWISE_OR,
		BITWISE_XOR,

		LOGICAL_AND,
		LOGICAL_OR,

		ASSIGN,
		COMMA;

		public static boolean isUnary(Op op) {
			return op == UNARY_MINUS || op == UNARY_LOGICAL_NOT || op == UNARY_BITWISE_NOT;
		}

		public static boolean isLeftAssoc(Op op) {
			return !isUnary(op) && op != ASSIGN && op != POWER && op != COMMA;
		}
	}

	class Const implements Expr {
		private final float value;

		public Const(float value) {
			this.value = value;
		}

		public float eval() {
			return this.value;
		}

		public String toString() {
			return "#" + this.value;
		}
	}

	class Var implements Expr {
		private float value;

		public Var(float value) {
			this.set(value);
		}

		public void set(float value) {
			this.value = value;
		}

		public float eval() {
			return this.value;
		}

		public String toString() {
			return "{" + this.value + "}";
		}
	}

	class Unary implements Expr {
		private final Op op;
		private final Expr arg;

		public Unary(Op op, Expr arg) {
			this.op = op;
			this.arg = arg;
		}

		public float eval() {
			switch (this.op) {
			case UNARY_MINUS:
				return -this.arg.eval();
			case UNARY_BITWISE_NOT:
				return ~((int) this.arg.eval());
			case UNARY_LOGICAL_NOT:
				return ((int) this.arg.eval()) == 0 ? 1 : 0;
			default:
				break;
			}
			return 0;
		}
	}

	class Binary implements Expr {
		private final Op op;
		private final Expr a;
		private final Expr b;

		public Binary(Op op, Expr a, Expr b) {
			this.op = op;
			this.a = a;
			this.b = b;
		}

		public float eval() {
			switch (this.op) {
			case POWER:
				return (float) Math.pow(this.a.eval(), this.b.eval());
			case MULTIPLY:
				return this.a.eval() * this.b.eval();
			case DIVIDE:
				return this.a.eval() / this.b.eval();
			case REMAINDER:
			case PLUS:
				return this.a.eval() + this.b.eval();
			case MINUS:
				return this.a.eval() - this.b.eval();
			case SHL:
				return (int) this.a.eval() << (int) this.b.eval();
			case SHR:
				return (int) this.a.eval() >>> (int) this.b.eval();
			case LT:
				return this.a.eval() < this.b.eval() ? 1 : 0;
			case LE:
				return this.a.eval() <= this.b.eval() ? 1 : 0;
			case GT:
				return this.a.eval() > this.b.eval() ? 1 : 0;
			case GE:
				return this.a.eval() >= this.b.eval() ? 1 : 0;
			case EQ:
				return this.a.eval() == this.b.eval() ? 1 : 0;
			case NE:
				return this.a.eval() != this.b.eval() ? 1 : 0;
			case BITWISE_AND:
				return (int) this.a.eval() & (int) this.b.eval();
			case BITWISE_OR:
				return (int) this.a.eval() | (int) this.b.eval();
			case BITWISE_XOR:
				return (int) this.a.eval() ^ (int) this.b.eval();
			case LOGICAL_AND:
				return this.a.eval() != 0 ? this.b.eval() : 0;
			case LOGICAL_OR:
				float cond = this.a.eval();
				return cond != 0 ? cond : this.b.eval();
			case ASSIGN:
				float rhs = this.b.eval();
				((Expr.Var) this.a).set(rhs);
				return rhs;
			case COMMA:
				this.a.eval();
				return this.b.eval();
			default:
				break;
			}
			return 0;
		}
	}

	interface Func<T> {
		public float eval(FuncContext<T> c);
	}

	public class FuncContext<T> implements Expr {
		protected final Func<T> f;
		protected final List<Expr> args;
		protected final Map<String, Var> vars;
		public T env;

		public FuncContext(Func<T> f, List<Expr> args, Map<String, Var> vars) {
			this.f = f;
			this.args = args;
			this.vars = vars;
		}

		public float eval() {
			return this.f.eval(this);
		}
	}

	class Builder {
		private final static int TOK_NUMBER = 1;
		private final static int TOK_WORD = 2;
		private final static int TOK_OP = 4;
		private final static int TOK_OPEN = 8;
		private final static int TOK_CLOSE = 16;

		private final static int PAREN_ALLOWED = 0;
		private final static int PAREN_EXPECTED = 1;
		private final static int PAREN_FORBIDDEN = 2;

		@SuppressWarnings("serial")
		public final static Map<String, Op> OPS = new HashMap<String, Op>() {
			{
				put("-u", Op.UNARY_MINUS);
				put("!u", Op.UNARY_LOGICAL_NOT);
				put("^u", Op.UNARY_BITWISE_NOT);
				put("**", Op.POWER);
				put("*", Op.MULTIPLY);
				put("/", Op.DIVIDE);
				put("%", Op.REMAINDER);
				put("+", Op.PLUS);
				put("-", Op.MINUS);
				put("<<", Op.SHL);
				put(">>", Op.SHR);
				put("<", Op.LT);
				put("<=", Op.LE);
				put(">", Op.GT);
				put(">=", Op.GE);
				put("==", Op.EQ);
				put("!=", Op.NE);
				put("&", Op.BITWISE_AND);
				put("^", Op.BITWISE_XOR);
				put("|", Op.BITWISE_OR);
				put("&&", Op.LOGICAL_AND);
				put("||", Op.LOGICAL_OR);
				put("=", Op.ASSIGN);
				put(",", Op.COMMA);
			}
		};

		public List<String> tokenize(String input) {
			int pos = 0;
			int expected = TOK_OPEN | TOK_NUMBER | TOK_WORD;
			List<String> tokens = new ArrayList<>();
			while (pos < input.length()) {
				String tok = "";
				char c = input.charAt(pos);
				if (Character.isWhitespace(c)) {
					pos++;
					continue;
				}
				if (Character.isDigit(c)) {
					if ((expected & TOK_NUMBER) == 0) {
						return null; // unexpected number
					}
					expected = TOK_OP | TOK_CLOSE;
					while ((c == '.' || Character.isDigit(c)) && pos < input.length()) {
						tok = tok + input.charAt(pos);
						pos++;
						if (pos < input.length()) {
							c = input.charAt(pos);
						} else {
							c = 0;
						}
					}
				} else if (Character.isLetter(c)) {
					if ((expected & TOK_WORD) == 0) {
						return null; // Unexpected identifier
					}
					expected = TOK_OP | TOK_OPEN | TOK_CLOSE;
					while ((Character.isLetter(c) || Character.isDigit(c) || c == '_') && pos < input.length()) {
						tok = tok + input.charAt(pos);
						pos++;
						if (pos < input.length()) {
							c = input.charAt(pos);
						} else {
							c = 0;
						}
					}
				} else if (c == '(' || c == ')') {
					tok = tok + c;
					pos++;
					if (c == '(' && (expected & TOK_OPEN) != 0) {
						expected = TOK_NUMBER | TOK_WORD | TOK_OPEN | TOK_CLOSE;
					} else if (c == ')' && (expected & TOK_CLOSE) != 0) {
						expected = TOK_OP | TOK_CLOSE;
					} else {
						return null; // Parens mismatch
					}
				} else {
					if ((expected & TOK_OP) == 0) {
						if (c != '-' && c != '^' && c != '!') {
							return null; // Missing operand
						}
						tok = tok + c + 'u';
						pos++;
					} else {
						String lastOp = null;
						while (!Character.isLetter(c) && !Character.isDigit(c) && !Character.isWhitespace(c) && c != '_'
								&& c != '(' && c != ')' && pos < input.length()) {
							if (OPS.containsKey(tok + input.charAt(pos))) {
								tok = tok + input.charAt(pos);
								lastOp = tok;
							} else if (lastOp == null) {
								tok = tok + input.charAt(pos);
							} else {
								break;
							}
							pos++;
							if (pos < input.length()) {
								c = input.charAt(pos);
							} else {
								c = 0;
							}
						}
						if (lastOp == null) {
							return null; // Bad operator
						}
					}
					expected = TOK_NUMBER | TOK_WORD | TOK_OPEN;
				}
				tokens.add(tok);
			}
			return tokens;
		}

		public Expr parse(String s, Map<String, Var> vars, @SuppressWarnings("rawtypes") Map<String, Func> funcs) {
			if (vars == null) {
				vars = new HashMap<>();
			}
			Stack<String> os = new Stack<>();
			Stack<Expr> es = new Stack<>();
			int paren = PAREN_ALLOWED;
			List<String> tokens = tokenize(s);
			if (tokens == null) {
				return null;
			}
			for (String token : tokens) {
				int parenNext = PAREN_ALLOWED;
				if (token.equals("(")) {
					if (paren == PAREN_EXPECTED) {
						os.push("{");
					} else if (paren == PAREN_ALLOWED) {
						os.push("(");
					} else {
						return null; // bad call
					}
				} else if (paren == PAREN_EXPECTED) {
					return null; // bad call
				} else if (token.equals(")")) {
					while (!os.isEmpty() && !os.peek().equals("(") && !os.peek().equals("{")) {
						Expr e = bind(os.pop(), es);
						if (e == null) {
							return null;
						}
						es.push(e);
					}
					if (os.isEmpty()) {
						return null; // Bad paren
					}
					if (os.pop().equals("{")) {
						Func<?> f = funcs.get(os.pop());
						List<Expr> args = new ArrayList<Expr>();
						if (!es.isEmpty()) {
							Expr e = es.pop();
							while (e != null) {
								if (e instanceof Binary) {
									Binary binExpr = (Binary) e;
									if (binExpr.op == Op.COMMA) {
										args.add(binExpr.a);
										e = binExpr.b;
										continue;
									}
								}
								args.add(e);
								break;
							}
						}
						es.push(new FuncContext<>(f, args, vars));
					}
					parenNext = PAREN_FORBIDDEN;
				} else if (parseFloat(token) != null) {
					es.push(new Const(parseFloat(token)));
					parenNext = PAREN_FORBIDDEN;
				} else if (funcs != null && funcs.containsKey(token)) {
					os.push(token);
					parenNext = PAREN_EXPECTED;
				} else if (OPS.containsKey(token)) {
					Op op = OPS.get(token);
					String o2 = os.isEmpty() ? null : os.peek();

					while (OPS.containsKey(o2) && ((Op.isLeftAssoc(op) && op.ordinal() > OPS.get(o2).ordinal())
							|| (op.ordinal() > OPS.get(o2).ordinal()))) {
						Expr e = bind(o2, es);
						if (e == null) {
							return null;
						}
						es.push(e);
						os.pop();
						o2 = os.isEmpty() ? null : os.peek();
					}
					os.push(token);
				} else {
					if (vars.containsKey(token)) {
						es.push(vars.get(token));
					} else {
						Var v = new Var(0);
						vars.put(token, v);
						es.push(v);
					}
					parenNext = PAREN_FORBIDDEN;
				}
				paren = parenNext;
			}
			if (paren == PAREN_EXPECTED) {
				return null; // Bad call
			}
			while (!os.isEmpty()) {
				String op = os.pop();
				if (op.equals("(") || op.equals(")")) {
					return null; // Bad paren
				}
				Expr e = bind(op, es);
				if (e == null) {
					return null;
				}
				es.push(e);
			}
			if (es.isEmpty()) {
				return new Const(0);
			} else {
				return es.pop();
			}
		}

		private Expr bind(String s, Stack<Expr> stack) {
			if (OPS.containsKey(s)) {
				Op op = OPS.get(s);
				if (Op.isUnary(op)) {
					if (!stack.isEmpty() && stack.peek() == null) {
						return null; // Operand missing
					}
					return new Unary(op, stack.pop());
				} else {
					Expr b = stack.pop();
					Expr a = stack.pop();
					if (a == null || b == null) {
						return null; // Operand missing
					}
					return new Binary(op, a, b);
				}
			} else {
				return null; // Bad call
			}
		}

		private Float parseFloat(String s) {
			try {
				return Float.parseFloat(s);
			} catch (NumberFormatException e) {
				return null;
			}
		}
	}
}