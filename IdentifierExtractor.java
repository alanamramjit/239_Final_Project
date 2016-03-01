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
		ASTParser parser = ASTParser.newParser(AST.JLS3); 
		HashSet<String> identifiers = new HashSet<String>();      

		//explore directory
		File folder = new File(args[0]);
		LinkedList<File> files =  new LinkedList<File>();	
		File[] parseDirectory = folder.listFiles();
		for(File f : parseDirectory)
			files.add(f);	

		while(!files.isEmpty()){		
			File curr = files.remove();
			if(curr.isDirectory()){
				parseDirectory = curr.listFiles();
				for(File f: parseDirectory)
					files.add(f);
			}
			else if(curr.canRead() && curr.getName().endsWith(".java")){
				System.out.println("Processing: " + curr.getName());				
				try{
					Scanner fr = new Scanner(curr);
					StringBuffer sb = new StringBuffer();
					while(fr.hasNextLine())
						sb.append(fr.nextLine());
					char [] source = sb.toString().toCharArray();
					parser.setSource(sb.toString().toCharArray());				
					parser.setKind(ASTParser.K_COMPILATION_UNIT);			
					CompilationUnit cu = (CompilationUnit) parser.createAST(null);  
					IdentifierVisitor iv = new IdentifierVisitor(cu);
					cu.accept(iv);
					HashSet<String> temp = iv.getIds();
					for(String id : temp)
						identifiers.add(id);
				}
				catch(FileNotFoundException fnf){}

			}
			else
				if(curr.getName().endsWith(".java"))
					System.err.println("Warning: file " + curr.getName() + " was not processed.");
		}

		for(String id : identifiers)
			System.out.println(id);

	}

}





class IdentifierVisitor extends ASTVisitor{

	private CompilationUnit cu;
	private HashSet<String> ids;

	public IdentifierVisitor(CompilationUnit cu){
		this.cu = cu;
		ids = new HashSet<String>();
	}

	public boolean visit(MethodDeclaration node){
		ids.add(node.getName().getIdentifier());
		return true;
	}

	public boolean visit(VariableDeclarationFragment node){
		ids.add(node.getName().getIdentifier());
		return true;
	}

	public boolean visit(TypeDeclaration node){
		ids.add(node.getName().getIdentifier());
		return true;
	}

	public boolean visit(VariableDeclaration node){
		ids.add(node.getName().getIdentifier());
		return true;
	}
	public boolean visit(VariableDeclarationExpression node){

		List<VariableDeclarationFragment> vdfs = node.fragments();
		for(VariableDeclarationFragment vdf : vdfs){
			ids.add(vdf.getName().getIdentifier());
		}
		return true;
	}
	

	public boolean visit(AnnotationTypeDeclaration node){
		ids.add(node.getName().getIdentifier());
		return true;
	}

	public boolean visit(AnnotationTypeMemberDeclaration node){
		ids.add(node.getName().getIdentifier());
		return true;
	}

	public boolean visit(EnumDeclaration node){
		ids.add(node.getName().getIdentifier());
		return true;
	}

	public boolean visit(EnumConstantDeclaration node){
		ids.add(node.getName().getIdentifier());
		return true;
	}

	public HashSet<String> getIds(){
		return ids;
	}
}
