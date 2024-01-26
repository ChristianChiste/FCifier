package factory;

import at.uibk.dps.afcl.Function;
import at.uibk.dps.afcl.functions.objects.DataIns;
import parser.FunctionDefinition;
import parser.PassedVariable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractFunctionFactory {

    abstract Function createFunction(final FunctionDefinition functionDefinition, final String previousFunctionName);

}
