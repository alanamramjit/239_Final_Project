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
		if(args.length != 1){
			System.err.println("Usage: java IdentifierExtractor <source-directory>");
			System.exit(1);
		}

		ArrayList<File> files =  new ArrayList<File>();
		File folder = new File(args[0]);
			files.add(folder);	
		while(!files.isEmpty()){		
			File curr = files.remove(0);
			if(curr.isDirectory()){
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
						cu = JavaParser.parse(in);
					}

					finally {
						in.close();
					}
					// prints the resulting compilation unit to default system output
					new MyVisitor(curr.getName()).visit(cu, null);


				}
				catch(FileNotFoundException fnf){}
			}
		}
	}
}

class MyVisitor extends VoidVisitorAdapter
{
	private final String file;
	public MyVisitor(String filename){
		file = filename;
	}

		
	public void visit(VariableDeclarator declarator, Object args){
		System.out.println(file + ":" + declarator.getId().getName());
		super.visit(declarator, args);
	}


	public void visit(MethodDeclaration n, Object arg) {
		System.out.println(file + ":" + n.getName());
		super.visit(n, arg);
	}

	public void visit(ClassOrInterfaceDeclaration n, Object arg){
		System.out.println(file + ":" + n.getName());
		super.visit(n, arg);
	}

	public void visit(EnumDeclaration n, Object arg){
		System.out.println(file + ":" + n.getName());
		super.visit(n, arg);
	}

	public void visit(EnumConstantDeclaration n, Object arg){
		System.out.println(file + ":" + n.getName());
		super.visit(n, arg);
	}

	public void visit(AnnotationDeclaration n, Object arg){
		System.out.println(file + ":" + n.getName());
		super.visit(n, arg);
	}
	public void visit(AnnotationMemberDeclaration n, Object arg){
		System.out.println(file + ":" + n.getName());
		super.visit(n, arg);
	}

}
