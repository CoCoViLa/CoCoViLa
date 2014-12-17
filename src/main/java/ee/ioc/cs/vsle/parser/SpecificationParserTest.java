package ee.ioc.cs.vsle.parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;

import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageLexer;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.MetaInterfaseContext;

public class SpecificationParserTest {

	public static void main(String[] args) throws IOException {
		
		Path p = Paths.get("D:\\workspaces\\Diplom\\CoCoViLa\\parserTest");
		File file = p.toFile();
		File[] files = file.listFiles();
		for (File file2 : files) {
			System.out.println("Parsing: " + file2.getName());
			CharStream input = new ANTLRFileStream(file2.getAbsolutePath());
			
			SpecificationLanguageLexer lexer = new SpecificationLanguageLexer(input);
			
			TokenStream token = new CommonTokenStream(lexer);
			SpecificationLanguageParser parser = new SpecificationLanguageParser(token);
			
			MetaInterfaseContext metaInterfase = parser.metaInterfase();
			System.out.println("DONE\n");		
		}
		
//		final SpecificationParserBaseVisitor<Integer> mathWalker = new SpecificationParserBaseVisitor<Integer>(){
//			@Override
//			public Integer visitMult(MultContext ctx) {
//				Integer op1 = visit(ctx.left);
//				Integer op2 = visit(ctx.right);
//				Integer res;
//				String op = ctx.op.getText();
//				if("*".equals(op))
//					res = op1 * op2;
//				else if("/".equals(op))
//					res = op1 / op2;
//				else if("mod".equals(op))
//					res = op1 % op2;
//				else
//					res = 0;
//				return res;
//			}
//			
//			@Override
//			public Integer visitSum(SumContext ctx) {
//				Integer op1 = visit(ctx.left);
//				Integer op2 = visit(ctx.right);
//				Integer res;
//				String op = ctx.op.getText();
//				if("+".equals(op))
//					res = op1 + op2;
//				else if("-".equals(op))
//					res = op1 - op2;
//				else
//					res = 0;
//				return res;
//			}
//			
//			@Override
//			public Integer visitItem(ItemContext ctx) {
//				return visit(ctx.term());
//			}
//			
//			@Override
//			public Integer visitExpr(ExprContext ctx) {
//				return visit(ctx.expression());
//			}
//			
//			@Override
//			public Integer visitTerm(TermContext ctx) {
//				return Integer.parseInt(ctx.getText());
//			}
//			
//			@Override
//			public Integer visitInver(InverContext ctx) {
//				Integer expr = visit(ctx.expression());
//				return -1 * expr;
//			}
//			
////			:	left = expression op=('*' | '/' | 'mod') right = expression	# mult
////			|	left = expression op=('+' | '-') right = expression			# sum
////			|	term														# item
////			|	'(' expression ')'											# expr
//		};
//		
//		SpecificationParserBaseListener secificationListener = new SpecificationParserBaseListener(){
//			@Override
//			public void enterEquation(EquationContext ctx) {
//				System.out.println("Found expr: " + ctx.getText());
//				System.out.println("\t L part = " + mathWalker.visit(ctx.left));
//				System.out.println("\t R part = " + mathWalker.visit(ctx.right));
//			}
//			
//			
//		};
//		
//		ParseTreeWalker walker = new ParseTreeWalker(); // create standard walker
//		walker.walk(secificationListener, specification);
		
	}

}
