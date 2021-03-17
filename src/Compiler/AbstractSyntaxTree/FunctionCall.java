package Compiler.AbstractSyntaxTree;

import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;

public class FunctionCall extends Node {
    Node name;
    Node arguments;

    public FunctionCall(Node name, Node args, int line, SymbolTable symbolTable) {
        this.name = name;
        this.arguments = args;
        setLine(line);

        checkFunction(symbolTable);
    }

    private void checkFunction(SymbolTable symbolTable) {
        if (name instanceof Identifier) {
            String funcName = ((Identifier) name).getName();
            if (funcName.equals("scanf") && arguments instanceof ExpressionList) {
                for(Node node : ((ExpressionList) arguments).getExpressions()) {
                    if (node instanceof UnaryOperator && ((UnaryOperator) node).getOperator().equals("&")) {
                        SymbolTableFiller.resolveInitialization(((UnaryOperator) node).getExpression(), symbolTable, node.getLine());
                    }
                }
            }
        }
    }

    @Override
    public Node getNameNode() {
        return name;
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "FunctionCall: ");
        if (name != null) name.traverse(indent + "    ");
        if (arguments != null) arguments.traverse(indent + "    ");
    }

}
