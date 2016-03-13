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
					MyVisitor mv = new MyVisitor(curr.getName(), args[1]);
					mv.visit(cu, null);
					mv.close();

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
	private FileWriter pw;
	private ArrayList<String> currMethod;
	private Stack<Integer> lastMethodEnd;
	private HashMap<String, HashSet<String>> method_map;	

	public MyVisitor(String filename, String output) throws FileNotFoundException, IOException{
		file = filename;
		this.output =  output;
		currMethod = new ArrayList<String>();
		all_ids = new FileWriter(output + "_all_ids.txt", true);
		method_ids = new FileWriter(output + "_method_ids.txt", true);
		pw = new FileWriter(output + "_method_map.txt", true);
		lastMethodEnd = new Stack<Integer>();
		method_map = new HashMap<String, HashSet<String>>();

	}


	public void visit(VariableDeclarator declarator, Object args){
		try{
			all_ids.write(file + ":" + declarator.getId().getName() + "\n");
			clearStack(declarator.getBeginLine());
			if(!lastMethodEnd.isEmpty()){
				for(String s : currMethod){
					method_ids.write(file + ":" + s + ":" + declarator.getId().getName() + "\n");
				}
			}


		}

		catch(IOException ioe){}
		super.visit(declarator, args);
	}


	public void visit(MethodDeclaration n, Object arg){
		try{

			clearStack(n.getBeginLine());
			if(!lastMethodEnd.isEmpty()){
				for(String s : currMethod){
					method_ids.write(file + ":" + s + ":" + n.getName() + "\n");
				}
			}
			lastMethodEnd.push(n.getEndLine());
			currMethod.add(n.getName());
			all_ids.write(file + ":" + n.getName() + "\n");

		}
		catch(IOException ioe){}


		super.visit(n, arg);
	}

	public void visit(MethodCallExpr n, Object arg){
		clearStack(n.getBeginLine());
		if(!lastMethodEnd.isEmpty()){
			for(String s : currMethod){
			try{	
				pw.write(file + ":" + s + ":" + n.getName() + "\n");	
			   }
			   catch(IOException ioe){}
			}
		}


	}

	public void visit(ClassOrInterfaceDeclaration n, Object arg){
		try{
			all_ids.write(file + ":" + n.getName() + "\n");
			clearStack(n.getBeginLine());

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

	private void clearStack(int currLine){
		while(!lastMethodEnd.isEmpty() && currLine > lastMethodEnd.peek()){
			lastMethodEnd.pop();
			currMethod.remove(currMethod.size() -1); 

		}

	}

	public void close(){

		try{
			all_ids.close();
			method_ids.close();
			pw.close();


		}
		catch(IOException ioe){}

	}
}
