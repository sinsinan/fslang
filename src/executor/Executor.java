package executor;

import ast.*;
import lexer.Token;

public class Executor {

    private String lastId;

    public Executor() {
    }

    public void execute(Expression program) throws Exception {
        Value v = this.defer(program, null);
        this.showValue(v);
    }

    private Value defer(Expression e, Env env) {
        return new Value(ValueType.Deferred, e, env);
    }

    private void showValue(Value v) throws Exception {
        if (v == null) {
            System.out.print("NULL");
            return;
        }
        switch (v.getType()) {
            case Number:
                System.out.print(v.getN());
                break;
            case Boolean:
                System.out.print(v.getB());
                break;
            case Character:
                System.out.print(v.getCh());
                break;
            case Nil:
                System.out.print("nil");
                break;
            case Empty:
                System.out.print("()");
                break;
            case List:
                System.out.print("(");
                this.showValue(v.getHd());
                System.out.println("::");
                this.showValue(v.getTl());
                System.out.print(")");
                break;
            case Function:
                System.out.print("function");
                break;
            case Deferred:
                this.force(v);
                showValue(v);
                break;
        }
    }

    private Value  force(Value v) throws Exception {
        if (v.getType().equals(ValueType.Deferred)) {
            Value v2 = this.eval(v.getExp(), v.getEnv());
            v.setValue(v2);
        }

        return v;
    }

    private Value eval(Expression exp, Env env) throws Exception {
        if (exp.getClass().equals(Ident.class)) {
            return this.applyEnv(env, ((Ident) exp).getId());
        } else if (exp.getClass().equals(NumCon.class)) {
            return new Value(((NumCon) exp).getN());
        } else if (exp.getClass().equals(BoolCon.class)) {
            return new Value(((BoolCon) exp).getB());
        } else if (exp.getClass().equals(CharCon.class)) {
            return new Value(((CharCon) exp).getCh());
        } else if (exp.getClass().equals(NilCon.class)) {
            return new Value(ValueType.Nil);
        } else if (exp.getClass().equals(EmptyCon.class)) {
            return new Value(ValueType.Empty);
        } else if (exp.getClass().equals(LambdaExp.class)) {
            return new Value(ValueType.Function, exp, env);
        } else if (exp.getClass().equals(Application.class)) {
            Value func = this.eval(((Application) exp).getFun(), env);
            if (func.getType().equals(ValueType.Function)) {
                return this.apply(func, defer(((Application) exp).getaParam(), env));
            } else {
                throw new Exception("apply ~fn ");
            }
        } else if (exp.getClass().equals(UnExp.class)) {
            return this.evalUnary(((UnExp) exp).getUnOpr(), this.eval(((UnExp) exp).getUnArg(), env));
        } else if (exp.getClass().equals(BinExp.class)) {
            if (((BinExp) exp).getBinOpr().equals(Token.CONSSY)) {
                return this.evalBinOpr(((BinExp) exp).getBinOpr(), this.defer(((BinExp) exp).getLeft(), env), this.defer(((BinExp) exp).getRight(), env));
            } else {
                return this.evalBinOpr(((BinExp) exp).getBinOpr(), this.eval(((BinExp) exp).getLeft(), env), this.eval(((BinExp) exp).getRight(), env));
            }
        } else if (exp.getClass().equals(IfExp.class)) {
            Value evaluatedExp1 = this.eval(((IfExp) exp).getE1(), env);
            if (evaluatedExp1.getType().equals(ValueType.Boolean)) {
                if (evaluatedExp1.getB()) {
                    return this.eval(((IfExp) exp).getE2(), env);
                } else {
                    return this.eval(((IfExp) exp).getE3(), env);
                }
            } else {
                throw new Exception("if ~bool  ");
            }
        } else if (exp.getClass().equals(Block.class)) {
            return this.eval(((Block) exp).getExp(), this.evalDecs(((Block) exp).getDecs(), env));
        } else {
            throw new Exception("Invalid Expression type: " + exp.getClass());
        }
    }

    private Env evalDecs(Expression decs, Env env) {
        if (decs == null) {
            return env;
        } else {
            if (decs.getClass().equals(DecList.class) && ((DecList) decs).getRecursive()) {
                Env localEnv = this.bind("-dummy----", null, env);
                localEnv.setNext(this.constructEnv(decs, localEnv, env));
                return localEnv;
            } else {
                return this.constructEnv(decs, env, env);
            }
        }
    }

    private Env constructEnv(Expression decs, Env local, Env global) {
        if (decs == null) {
            return global;
        } else {
            return this.bind(((Decln) ((DecList) decs).getHd()).getName(), this.defer(((Decln) ((DecList) decs).getHd()).getVal(), local), this.constructEnv(((DecList) decs).getTl(), local, global));
        }
    }

    private Value evalBinOpr(Token binOpr, Value left, Value right) throws Exception {
        switch (binOpr) {
            case EQ:
                return new Value(this.getAbsForRelationalOperand(left) == this.getAbsForRelationalOperand(right));
            case NE:
                return new Value(this.getAbsForRelationalOperand(left) != this.getAbsForRelationalOperand(right));
            case LT:
                return new Value(this.getAbsForRelationalOperand(left) < this.getAbsForRelationalOperand(right));
            case LE:
                return new Value(this.getAbsForRelationalOperand(left) <= this.getAbsForRelationalOperand(right));
            case GT:
                return new Value(this.getAbsForRelationalOperand(left) > this.getAbsForRelationalOperand(right));
            case GE:
                return new Value(this.getAbsForRelationalOperand(left) >= this.getAbsForRelationalOperand(right));
            case PLUS:
            case MINUS:
            case TIMES:
            case OVER:
                if (left.getType().equals(ValueType.Number) && right.getType().equals(ValueType.Number)) {
                    switch (binOpr) {
                        case PLUS:
                            return new Value(left.getN() + right.getN());
                        case MINUS:
                            return new Value(left.getN() - right.getN());
                        case TIMES:
                            return new Value(left.getN() * right.getN());
                        case OVER:
                            return new Value(left.getN() / right.getN());
                    }
                } else {
                    throw new Exception("arith opr ");
                }
            case ANDSY:
            case ORSY:
                if (left.getType().equals(ValueType.Boolean) && right.getType().equals(ValueType.Boolean)) {
                    switch (binOpr) {
                        case ANDSY:
                            return new Value(left.getB() && right.getB());
                        case ORSY:
                            return new Value(left.getB() || right.getB());
                    }
                } else {
                    throw new Exception("bool opr  ");
                }
            case CONSSY:
                return new Value(left, right);
        }
        throw new Exception("Invalid binary operator: " + binOpr);
    }

    private double getAbsForRelationalOperand(Value operand) throws Exception {
        if (operand.getType().equals(ValueType.Number)) {
            return operand.getN();
        } else if (operand.getType().equals(ValueType.Boolean)) {
            return operand.getB() ? 1 : 0;
        } else if (operand.getType().equals(ValueType.Character)) {
            return operand.getCh();
        }
        throw new Exception("rel ops   ");
    }

    private Value evalUnary(Token unOpr, Value eval) throws Exception {
        switch (unOpr) {
            case MINUS:
                if (eval.getType().equals(ValueType.Number)) {
                    return new Value(-eval.getN());
                } else {
                    throw new Exception("- non int ");
                }
            case NOTSY:
                if (eval.getType().equals(ValueType.Boolean)) {
                    return new Value(!eval.getB());
                } else {
                    throw new Exception("not ~bool ");
                }
            case HDSY:
                if (eval.getType().equals(ValueType.List)) {
                    this.force(eval.getHd());
                    return eval.getHd();
                } else {
                    throw new Exception("hd ~list  ");
                }
            case TLSY:
                if (eval.getType().equals(ValueType.List)) {
                    this.force(eval.getTl());
                    return eval.getTl();
                } else {
                    throw new Exception("tl ~list  ");
                }
            case NULLSY:
                if (eval.getType().equals(ValueType.List)) {
                    return new Value(false);
                } else if (eval.getType().equals(ValueType.Nil)) {
                    return new Value(true);
                } else {
                    throw new Exception("null ~list");
                }
        }
        throw new Exception("Invalid unOpr: " + unOpr);
    }

    private Value apply(Value func, Value value) throws Exception {
        if (((LambdaExp) func.getExp()).getfParam().getClass().equals(EmptyCon.class)) {
            this.force(value);
            if (value.getType().equals(ValueType.Empty)) {
                return this.eval(((LambdaExp) func.getExp()).getBody(), func.getEnv());
            } else {
                throw new Exception("L().e exp ");
            }
        } else {
            return this.eval(((LambdaExp) func.getExp()).getBody(), this.bind(((Ident) (((LambdaExp) func.getExp()).getfParam())).getId(), value, func.getEnv()));
        }
    }

    private Env bind(String id, Value defer, Env env) {
        return new Env(id, defer, env);
    }

    private Value applyEnv(Env env, String id) throws Exception {
        this.lastId = id;
        if (env == null) {
            throw new Exception("Last id: " + lastId + " not found");
        } else if (id.equals(env.getId())) {
            this.force(env.getV());
            return env.getV();
        } else {
            return this.applyEnv(env.getNext(), id);
        }
    }
}
