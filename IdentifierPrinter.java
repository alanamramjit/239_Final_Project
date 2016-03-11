import java.io.*;
import java.util.*;
import com.github.javaparser.*;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.internal.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.visitor.*;

public class IdentifierPrinter {

	public static void main(String[] args) throws Exception {
		if(args.length != 2){
			System.err.println("Usage: java IdentifierPrinter <source_directory> <output_prefix>");
			System.exit(1);
		}

		ArrayList<File> files =  new ArrayList<File>();
		File folder = new File(args[0]);
		files.add(folder);	
		while(!files.isEmpty()){		
			File curr = files.remove(0);
			if(curr.isDirectory()){
				System.out.println("Entering " + curr.getName());
				File[] parseDirectory = curr.listFiles();
				for(File f : parseDirectory)
					files.add(f);	
			}
			else if(curr.getName().endsWith(".java")){				
				try{
					FileInputStream in = new FileInputStream(curr);
					CompilationUnit cu;
					try {
						// parse the file
						System.out.println("Parsing " + curr.getName());
						cu = JavaParser.parse(in);
					}

					finally {
						in.close();
					}
					// prints the resulting compilation unit to default system output
					new MyVisitor(curr.getName(), args[1]).visit(cu, null);


				}
				catch(FileNotFoundException fnf){}
			}
		}
	}
}

class MyVisitor extends VoidVisitorAdapter 
{
	private final String file;
	private String output;
	private FileWriter all_ids;
	private FileWriter method_ids;
	private FileWriter method_map;
	private String currMethod;
	private int lastMethodEnd;

	public MyVisitor(String filename, String output) throws FileNotFoundException, IOException{
		file = filename;
		this.output =  output;
		currMethod = "";
		all_ids = new FileWriter(output + "_all_ids.txt", true);
		method_ids = new FileWriter(output + "_method_ids.txt", true);
		method_map = new FileWriter(output + "_method_graph.txt", true);
		lastMethodEnd = Integer.MAX_VALUE;

	}


	public void visit(VariableDeclarator declarator, Object args){
		try{
			all_ids.write(file + ":" + declarator.getId().getName() + "\n");
			if(declarator.getBeginLine() < lastMethodEnd)
				method_ids.write(file + ":" + currMethod + ":" + declarator.getId().getName() + "\n");
		}
		catch(IOException ioe){}
		super.visit(declarator, args);
	}


	public void visit(MethodDeclaration n, Object arg){
		try{
			all_ids.write(file + ":" + n.getName() + "\n");

		}	
		catch(IOException ioe){}
		if(n.getBeginLine() < lastMethodEnd && lastMethodEnd != Integer.MAX_VALUE)
			System.out.println("ALERT " + n.getName() + " declared inside of " + currMethod);


		currMethod = file + ":" + n.getName();
		lastMethodEnd = n.getEndLine();		
		super.visit(n, arg);
	}

	public void visit(MethodCallExpr n, Object arg){
		try{
			if(n.getBeginLine() < lastMethodEnd)
				method_map.write(currMethod + ":" + n.getName()+"\n");		
		}
		catch(IOException ioe){}

	}

	public void visit(ClassOrInterfaceDeclaration n, Object arg){
		try{
			all_ids.write(file + ":" + n.getName() + "\n");
			super.visit(n, arg);	}
		catch(IOException ioe){}
	}

	public void visit(EnumDeclaration n, Object arg){
		try{
			all_ids.write(file + ":" + n.getName());
			super.visit(n, arg);	}
		catch(IOException ioe){}
	}

	public void visit(EnumConstantDeclaration n, Object arg){
		try{
			all_ids.write(file + ":" + n.getName());
			super.visit(n, arg);
		}
		catch(IOException ioe){}
	}

	public void visit(AnnotationDeclaration n, Object arg){
		try{
			all_ids.write(file + ":" + n.getName());
			super.visit(n, arg);
		}
		catch(IOException ioe){}
	}
	public void visit(AnnotationMemberDeclaration n, Object arg){
		try{
			all_ids.write(file + ":" + n.getName());
			super.visit(n, arg);
		}
		catch(IOException ioe){}
	}

}
