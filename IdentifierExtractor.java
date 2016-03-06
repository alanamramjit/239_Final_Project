import java.util.*;
import org.eclipse.jdt.core.dom.*;
import java.io.*;

public class IdentifierExtractor{
	
	public static void main(String [] args){

		//check for directory
		if(args.length != 1){
			System.err.println("Usage: java IdentifierExtractor <source-directory>");
			System.exit(1);
		}

		//create parser and set of identifiers
		
		ArrayList<HashSet<String>> identifiers = new ArrayList<HashSet<String>>();

		//explore directory
		File folder = new File(args[0]);
		LinkedList<File> files =  new LinkedList<File>();	
		File[] parseDirectory = folder.listFiles();
		for(File f :parseDirectory)
			files.add(f);	

		int count = 0;
		while(!files.isEmpty()){
			File curr = files.remove();
			if(curr.isDirectory()){
				parseDirectory = curr.listFiles();
				for(File f:parseDirectory)
					files.add(f);
			}
			else if(curr.canRead() && curr.getName().endsWith(".java")){
				System.out.println("Processing: " + curr.getName());		
				try{
					ASTParser parser = ASTParser.newParser(AST.JLS3); 
					Scanner fr = new Scanner(curr);
					StringBuffer sb = new StringBuffer();
					while(fr.hasNextLine())
						sb.append(fr.nextLine());
					char [] source = sb.toString().toCharArray();
					parser.setSource(sb.toString().toCharArray());				
					parser.setKind(ASTParser.K_COMPILATION_UNIT);			
					CompilationUnit cu = (CompilationUnit) parser.createAST(null);  
					IdentifierVisitor iv = new IdentifierVisitor(curr.getName());
					cu.accept(iv);
			

				}
				catch(FileNotFoundException fnf){}

			}
			else
				if(curr.getName().endsWith(".java"))
					System.err.println("Warning: file " + curr.getName() + " was not processed.");
		}

		System.out.println("---->"+count);
	}

}





class IdentifierVisitor extends ASTVisitor{

	private String file;

	public IdentifierVisitor(String file){
		this.file = file;
	}

	public boolean visit(MethodDeclaration node){
		System.out.println(file + ":" + node.getName().getIdentifier());
		return true;
	}

	public boolean visit(VariableDeclarationFragment node){
		System.out.println(file + ":" + node.getName().getIdentifier());
		return true;
	}

	public boolean visit(TypeDeclaration node){
		System.out.println(file + ":" + node.getName().getIdentifier());
		return true;
	}

	public boolean visit(VariableDeclaration node){
		System.out.println(file + ":" + node.getName().getIdentifier());
		return true;
	}
	public boolean visit(VariableDeclarationExpression node){

		List<VariableDeclarationFragment> vdfs = node.fragments();
		for(VariableDeclarationFragment vdf : vdfs){
			System.out.println(file + ":" + vdf.getName().getIdentifier());
		}
		return true;
	}
	

	public boolean visit(AnnotationTypeDeclaration node){
		System.out.println(file + ":" + node.getName().getIdentifier());
		return true;
	}

	public boolean visit(AnnotationTypeMemberDeclaration node){
		System.out.println(file + ":" + node.getName().getIdentifier());
		return true;
	}

	public boolean visit(EnumDeclaration node){
		System.out.println(file + ":" + node.getName().getIdentifier());
		return true;
	}

	public boolean visit(EnumConstantDeclaration node){
		System.out.println(file + ":" + node.getName().getIdentifier());
		return true;
	}
	public boolean visit(FieldDeclaration node){
		List<VariableDeclarationFragment> vdfs = node.fragments();
		for(VariableDeclarationFragment vdf : vdfs){
			System.out.println(file + ":" + vdf.getName().getIdentifier());
		}
		return true;
	}

	public boolean visit(FieldAccess node){
		System.out.println(file + ":" + node.getName().getIdentifier());
		return true;
	}

}


