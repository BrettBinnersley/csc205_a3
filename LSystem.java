/* LSystem.java

   A parser for L-Systems.

   B. Bird - 02/09/2016
*/

import java.util.regex.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;


public class LSystem{

	private static final int FLAG_EVEN = 1;
	private static final int FLAG_ODD = 2;

	private static class Rule{
		char rule;
		String substitution;
		int lifetime;
		int flags;
		
		public Rule(char rule_char, String substitution, int lifetime, int flags){
			this.rule = rule_char;
			this.substitution = substitution;
			this.lifetime = lifetime;
			this.flags = flags;
		}
	}
	
	private LSystem(){
		axiom = null;
		rules = new ArrayList<Rule>();
	}
	
	private String axiom;
	private ArrayList<Rule> rules;
	
	
	private String GenerateRecursive(int max_iterations, int current_iteration, String input){
		String output_string = "";
		for (int i = 0; i < input.length(); i++){
			char c = input.charAt(i);
			if (current_iteration < max_iterations){
				boolean rule_found = false;
				for(Rule rule: rules){
					if (rule.rule != c)
						continue;
					if( (rule.lifetime > 0 && rule.lifetime < current_iteration) ||
						(rule.lifetime < 0 && max_iterations + rule.lifetime <= current_iteration) ||
						((current_iteration % 2 == 1) && (rule.flags & FLAG_EVEN) != 0) ||
						((current_iteration % 2 == 0) && (rule.flags & FLAG_ODD) != 0)
					  )
						continue;
					rule_found = true;
					output_string += GenerateRecursive(max_iterations,current_iteration+1,rule.substitution);
					break;
				}
				if (rule_found)
					continue;
			}
			output_string += c;
		}
		return output_string;
	}
	
	public String GenerateSystemString(int iterations){
		return GenerateRecursive(iterations, 0, axiom);
	}
	
	public static LSystem ParseFile(String filename){
		Scanner s;
		try{
			s = new Scanner(new File(filename));
		} catch(java.io.FileNotFoundException e){
			System.err.printf("Unable to open %s\n",filename);
			return null;
		}
		LSystem L = new LSystem();
		
		L.axiom = null;
		
		String patternString = "\\s*([0-9]+)?\\s*([%^]+)?\\s*([A-Za-z\\[\\]])\\s*=\\s*(.*)";
		Pattern P = Pattern.compile(patternString);
		
		while(s.hasNextLine()){
			String line = s.nextLine().trim();
			if (line.equals("") || line.charAt(0) == '#')
				continue;
			if (L.axiom == null){
				L.axiom = line;
				continue;
			}
			Matcher M = P.matcher(line);
			if (!M.matches())
				return null;
			int lifetime = 0;
			if (M.group(1) != null)
				lifetime = Integer.parseInt(M.group(1));
			String flagString = M.group(2);
			int flags = 0;
			if(flagString != null){
				for(int i = 0; i < flagString.length(); i++){
					switch(flagString.charAt(i)){
						case '%':
							flags |= FLAG_EVEN;
							break;
						case '^':
							flags |= FLAG_ODD;
							break;
						default:
							break;
					}
				}
			}
			char rule_char = M.group(3).charAt(0);
			String substitution = M.group(4);
			L.rules.add(new Rule(rule_char, substitution, lifetime, flags));
		}
		if (L.axiom == null)
			return null;
		return L;
	}
	
}