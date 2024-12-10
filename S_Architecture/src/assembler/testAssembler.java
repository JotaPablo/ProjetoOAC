package assembler;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class testAssembler {
		

	@Test
	public void testProcessCommand() {

		/*
		 *  Tabela de instruções, com base nos microprogramas especificados
		 *  Código    Comando       Argumento
		 *  0         add           %<regA> %<regB>       -> RegB <- RegA + RegB
		 *  1         add           <mem> %<regA>        -> RegA <- memória[mem] + RegA
		 *  2         add           %<regA> <mem>        -> Memória[mem] <- RegA + memória[mem]
		 *  3         add           imm %<regA>          -> RegA <- imm + RegA
		 *  4         sub           <regA> <regB>        -> RegB <- RegA - RegB
		 *  5         sub           <mem> %<regA>        -> RegA <- memória[mem] - RegA
		 *  6         sub           %<regA> <mem>        -> Memória[mem] <- RegA - memória[mem]
		 *  7         sub           imm %<regA>          -> RegA <- imm - RegA
		 *  8         move          <mem> %<regA>        -> RegA <- memória[mem]
		 *  9         move          %<regA> <mem>        -> Memória[mem] <- RegA
		 *  10        move          %<regA> %<regB>      -> RegB <- RegA
		 *  11        move          imm %<regA>          -> RegA <- immediate
		 *  12        inc           %<regA>              -> RegA++
		 *  13        jmp           <mem>                -> PC <- mem (desvio incondicional)
		 *  14        jn            <mem>                -> se última operação<0 então PC <- mem (desvio condicional)
		 *  15        jz            <mem>                -> se última operação=0 então PC <- mem (desvio condicional)
		 *  16        jeq           %<regA> %<regB> <mem> -> se RegA == RegB então PC <- mem (desvio condicional)
		 *  17        jneq          %<regA> %<regB> <mem> -> se RegA != RegB então PC <- mem (desvio condicional)
		 *  18        jgt           %<regA> %<regB> <mem> -> se RegA > RegB então PC <- mem (desvio condicional)
		 *  19        jlw           %<regA> %<regB> <mem> -> se RegA < RegB então PC <- mem (desvio condicional)
		 *  20        call          <mem>                -> PC <- mem, antes de desviar, empilha o endereço de retorno (endereço da instrução imediatamente posterior ao call)
		 *  21        ret           -                     -> PC <- pop() (desvio incondicional)
		 */

	    Assembler ass = new Assembler();
	    String commandLine[] = new String[4];
	    ArrayList<String> returnedObj = new ArrayList<>();

	    // Teste 0: add %<regA> %<regB>
	    returnedObj = new ArrayList<>();
	    ass = new Assembler();
	    commandLine[0] = "add";
	    commandLine[1] = "%RPG0";
	    commandLine[2] = "%RPG1";
	    ass.proccessCommand(commandLine);
	    returnedObj = ass.getObjProgram();
	    assertEquals("0", returnedObj.get(0)); // O código de add %regA %regB é 0
	    assertEquals("%RPG0", ass.getObjProgram().get(1));
	    assertEquals("%RPG1", ass.getObjProgram().get(2));
	    assertEquals(3, ass.getObjProgram().size()); // Apenas tres linhas: o comando e o endereço
	    
	 // Terceiro teste: add <addr> %<regB>
	    returnedObj = new ArrayList<>();
	    ass = new Assembler();
	    commandLine[0] = "add";
	    commandLine[1] = "var1";
	    commandLine[2] = "%RPG1";
	    ass.proccessCommand(commandLine);
	    returnedObj = ass.getObjProgram();
	    assertEquals("1", returnedObj.get(0)); // O código de add <addr> %regB é 1
	    assertEquals("&var1", ass.getObjProgram().get(1));
	    assertEquals("%RPG1", ass.getObjProgram().get(2));
	    assertEquals(3, ass.getObjProgram().size()); // Apenas tres linhas: o comando e o endereço
	    
	 // Segundo teste: add %<regA> <addr> 
	    returnedObj = new ArrayList<>();
	    ass = new Assembler();
	    commandLine[0] = "add";
	    commandLine[1] = "%RPG1";
	    commandLine[2] = "var2";
	    ass.proccessCommand(commandLine);
	    returnedObj = ass.getObjProgram();
	    assertEquals("2", returnedObj.get(0)); // O código de add %<regA> <addr> é 2 
	    assertEquals("%RPG1", ass.getObjProgram().get(1));
	    assertEquals("&var2", ass.getObjProgram().get(2));
	    assertEquals(3, ass.getObjProgram().size()); // Apenas tres linhas: o comando e o endereço
	    
	    //Terceiro teste: add imm %<regA> 
	    returnedObj = new ArrayList<>();
	    ass = new Assembler();
	    commandLine[0] = "add";
	    commandLine[1] = "127";
	    commandLine[2] = "%RPG1";
	    ass.proccessCommand(commandLine);
	    returnedObj = ass.getObjProgram();
	    assertEquals("3", returnedObj.get(0)); // O código de add %<regA> <addr> é 3
	    assertEquals("127", ass.getObjProgram().get(1));
	    assertEquals("%RPG1", ass.getObjProgram().get(2));
	    assertEquals(3, ass.getObjProgram().size()); // Apenas tres linhas: o comando e o endereço
	    
	    
	    //Quarto teste: sub %<regA> %<regB>
	    returnedObj = new ArrayList<>();
	    ass = new Assembler();
	    commandLine[0] = "sub";
	    commandLine[1] = "%RPG0";
	    commandLine[2] = "%RPG1";
	    ass.proccessCommand(commandLine);
	    returnedObj = ass.getObjProgram();
	    assertEquals("4", returnedObj.get(0)); // O código de sub %regA %regB é 4
	    assertEquals("%RPG0", ass.getObjProgram().get(1));
	    assertEquals("%RPG1", ass.getObjProgram().get(2));
	    assertEquals(3, ass.getObjProgram().size()); // Apenas tres linhas: o comando e o endereço
	    
	    //Quinto teste: sub <addr> %<regB>
	    returnedObj = new ArrayList<>();
	    ass = new Assembler();
	    commandLine[0] = "sub";
	    commandLine[1] = "var1";
	    commandLine[2] = "%RPG1";
	    ass.proccessCommand(commandLine);
	    returnedObj = ass.getObjProgram();
	    assertEquals("5", returnedObj.get(0)); // O código de sub <addr> %regB é 5
	    assertEquals("&var1", ass.getObjProgram().get(1));
	    assertEquals("%RPG1", ass.getObjProgram().get(2));
	    assertEquals(3, ass.getObjProgram().size()); // Apenas tres linhas: o comando e o endereço
	    
	    //Sexto teste: sub %<regA> <addr> 
	    returnedObj = new ArrayList<>();
	    ass = new Assembler();
	    commandLine[0] = "sub";
	    commandLine[1] = "%RPG1";
	    commandLine[2] = "var2";
	    ass.proccessCommand(commandLine);
	    returnedObj = ass.getObjProgram();
	    assertEquals("6", returnedObj.get(0)); // O código de sub %<regA> <addr> é 6 
	    assertEquals("%RPG1", ass.getObjProgram().get(1));
	    assertEquals("&var2", ass.getObjProgram().get(2));
	    assertEquals(3, ass.getObjProgram().size()); // Apenas tres linhas: o comando e o endereço
	    
	    //Setimo teste: sub imm %<regA> 
	    returnedObj = new ArrayList<>();
	    ass = new Assembler();
	    commandLine[0] = "sub";
	    commandLine[1] = "127";
	    commandLine[2] = "%RPG1";
	    ass.proccessCommand(commandLine);
	    returnedObj = ass.getObjProgram();
	    assertEquals("7", returnedObj.get(0)); // O código de sub %<regA> <addr> é 7 
	    assertEquals("127", ass.getObjProgram().get(1));
	    assertEquals("%RPG1", ass.getObjProgram().get(2));
	    assertEquals(3, ass.getObjProgram().size()); // Apenas tres linhas: o comando e o endereço
	    
	    //Oitavo teste: move <addr> %<regB>
	    returnedObj = new ArrayList<>();
	    ass = new Assembler();
	    commandLine[0] = "move";
	    commandLine[1] = "var1";
	    commandLine[2] = "%RPG1";
	    ass.proccessCommand(commandLine);
	    returnedObj = ass.getObjProgram();
	    assertEquals("8", returnedObj.get(0)); // O código de move <addr> %regB é 8
	    assertEquals("&var1", ass.getObjProgram().get(1));
	    assertEquals("%RPG1", ass.getObjProgram().get(2));
	    assertEquals(3, ass.getObjProgram().size()); // Apenas tres linhas: o comando e o endereço
	    
	    //Nono teste: move %<regA> <addr> 
	    returnedObj = new ArrayList<>();
	    ass = new Assembler();
	    commandLine[0] = "move";
	    commandLine[1] = "%RPG1";
	    commandLine[2] = "var2";
	    ass.proccessCommand(commandLine);
	    returnedObj = ass.getObjProgram();
	    assertEquals("9", returnedObj.get(0)); // O código de move %<regA> <addr> é 9 
	    assertEquals("%RPG1", ass.getObjProgram().get(1));
	    assertEquals("&var2", ass.getObjProgram().get(2));
	    assertEquals(3, ass.getObjProgram().size()); // Apenas tres linhas: o comando e o endereço
	    
	 
	   //Décimo teste: move %<regA> %<regB>
	    returnedObj = new ArrayList<>();
	    ass = new Assembler();
	    commandLine[0] = "move";
	    commandLine[1] = "%RPG0";
	    commandLine[2] = "%RPG1";
	    ass.proccessCommand(commandLine);
	    returnedObj = ass.getObjProgram();
	    assertEquals("10", returnedObj.get(0)); // O código de move %regA %regB é 10
	    assertEquals("%RPG0", ass.getObjProgram().get(1));
	    assertEquals("%RPG1", ass.getObjProgram().get(2));
	    assertEquals(3, ass.getObjProgram().size()); // Apenas tres linhas: o comando e o endereço
	    
	    //Décimo-primeiro: move <imm> %regB
	    returnedObj = new ArrayList<>();
	    ass = new Assembler();
	    commandLine[0] = "move";
	    commandLine[1] = "127";
	    commandLine[2] = "%RPG1";
	    ass.proccessCommand(commandLine);
	    returnedObj = ass.getObjProgram();
	    assertEquals("11", returnedObj.get(0)); // O código de move <imm> %regB é 11
	    assertEquals("127", ass.getObjProgram().get(1));
	    assertEquals("%RPG1", ass.getObjProgram().get(2));
	    assertEquals(3, ass.getObjProgram().size()); // Apenas duas linhas: o comando e o endereço

	    //Decimo segundo: inc %<regA>
	    returnedObj = new ArrayList<>();
	    ass = new Assembler();
	    commandLine[0] = "inc";
	    commandLine[1] = "%RPG3";
	    ass.proccessCommand(commandLine);
	    returnedObj = ass.getObjProgram();
	    assertEquals("12", returnedObj.get(0)); // O código de inc é 12
	    assertEquals("%RPG3", ass.getObjProgram().get(1));
	    assertEquals(2, ass.getObjProgram().size()); // Apenas duas linhas: o comando e o endereço
	    
	    //Decimo terceiro: jmp <addr>               
	    returnedObj = new ArrayList<>();
	    ass = new Assembler();
	    commandLine[0] = "jmp";
	    commandLine[1] = "var1";
	    ass.proccessCommand(commandLine);
	    returnedObj = ass.getObjProgram();
	    assertEquals("13", returnedObj.get(0)); // O código de jmp é 13
	    assertEquals("&var1", ass.getObjProgram().get(1));
	    assertEquals(2, ass.getObjProgram().size()); // Apenas duas linhas: o comando e o endereço


        //Decimo quarto: jn <addr>
	    returnedObj = new ArrayList<>();
	    ass = new Assembler();
	    commandLine[0] = "jn";
	    commandLine[1] = "var1";
	    ass.proccessCommand(commandLine);
	    returnedObj = ass.getObjProgram();
	    assertEquals("14", returnedObj.get(0)); // O código de jn é 14
	    assertEquals("&var1", ass.getObjProgram().get(1));
	    assertEquals(2, ass.getObjProgram().size()); // Apenas duas linhas: o comando e o endereço
	    
        //Decimo quinto: jz <addr>
	    returnedObj = new ArrayList<>();
	    ass = new Assembler();
	    commandLine[0] = "jz";
	    commandLine[1] = "var1";
	    ass.proccessCommand(commandLine);
	    returnedObj = ass.getObjProgram();
	    assertEquals("15", returnedObj.get(0)); // O código de jz <addr> é 15
	    assertEquals("&var1", ass.getObjProgram().get(1));
	    assertEquals(2, ass.getObjProgram().size()); // Apenas duas linhas: o comando e o endereço


        //Decimo sexto: jeq %regA %regB <addr>
        returnedObj = new ArrayList<>();
	    ass = new Assembler();
	    commandLine[0] = "jeq";
	    commandLine[1] = "%RPG0";
        commandLine[2] = "%RPG1";
        commandLine[3] = "var1";
	    ass.proccessCommand(commandLine);
	    returnedObj = ass.getObjProgram();
	    assertEquals("16", returnedObj.get(0)); // O código de jn é 16
	    assertEquals("%RPG0", ass.getObjProgram().get(1));
        assertEquals("%RPG1", ass.getObjProgram().get(2));
        assertEquals("&var1", ass.getObjProgram().get(3));
	    assertEquals(4, ass.getObjProgram().size()); // quatro linhas: o comando, o registrador A, o registrador B e o endereço
	  
  	   //Décimo sétimo: jneq %regA %regB <addr>
  		returnedObj = new ArrayList<>();
  		ass = new Assembler();
  		commandLine[0] = "jneq";
  		commandLine[1] = "%RPG0";
  		commandLine[2] = "%RPG1";
  		commandLine[3] = "label";
  		ass.proccessCommand(commandLine);
  		returnedObj = ass.getObjProgram();
  		assertEquals("17", returnedObj.get(0)); //the jneq %regA %regB <addr> code is 17
  		assertEquals("%RPG0", ass.getObjProgram().get(1));
  		assertEquals("%RPG1", ass.getObjProgram().get(2));
  		assertEquals("&label", ass.getObjProgram().get(3));
  		assertEquals(4, ass.getObjProgram().size()); // 
  	
  		//Décimo oitavo: jgt %<regA> %<regB> <mem>
  		returnedObj = new ArrayList<>();
  		ass = new Assembler();
  		commandLine[0] = "jgt";
  		commandLine[1] = "%RPG0";
  		commandLine[2] = "%RPG1";
  		commandLine[3] = "label2";
  		ass.proccessCommand(commandLine);
  		returnedObj = ass.getObjProgram();
  		assertEquals("18", returnedObj.get(0)); //the jgt %regA %regB <addr> code is 18
  		assertEquals("%RPG0", ass.getObjProgram().get(1));
  		assertEquals("%RPG1", ass.getObjProgram().get(2));
  		assertEquals("&label2", ass.getObjProgram().get(3));
  		assertEquals(4, ass.getObjProgram().size()); // 
  		
  		//Décimo nono: jlw %<regA> %<regB> <mem>
  		returnedObj = new ArrayList<>();
  		ass = new Assembler();
  		commandLine[0] = "jlw";
  		commandLine[1] = "%RPG2";
  		commandLine[2] = "%RPG1";
  		commandLine[3] = "label3";
  		ass.proccessCommand(commandLine);
  		returnedObj = ass.getObjProgram();
  		assertEquals("19", returnedObj.get(0)); //the jlw %<regA> %<regB> <mem> code is 19
  		assertEquals("%RPG2", ass.getObjProgram().get(1));
  		assertEquals("%RPG1", ass.getObjProgram().get(2));
  		assertEquals("&label3", ass.getObjProgram().get(3));
  		assertEquals(4, ass.getObjProgram().size()); //
  		
  	    //Vigésimo teste : call <mem>
	    returnedObj = new ArrayList<>();
	    ass = new Assembler();
	    commandLine[0] = "call";
	    commandLine[1] = "label4";
	    ass.proccessCommand(commandLine);
	    returnedObj = ass.getObjProgram();
	    assertEquals("20", returnedObj.get(0)); // O código de call <mem> é 20
	    assertEquals("&label4", ass.getObjProgram().get(1));
	    assertEquals(2, ass.getObjProgram().size()); // Apenas duas linhas: o comando e o endereço
	    
	    //Vigésimo primeiro: ret
	    returnedObj = new ArrayList<>();
	    ass = new Assembler();
	    commandLine[0] = "ret";
	    ass.proccessCommand(commandLine);
	    returnedObj = ass.getObjProgram();
	    assertEquals("21", returnedObj.get(0)); // O código de ret é 21
	    assertEquals(1, ass.getObjProgram().size()); // Apenas duas linhas: o comando e o endereço
  	
	 

	    // Teste final: um pequeno programa
	    returnedObj = new ArrayList<>();
	    ass = new Assembler();
	    // Comandos
        commandLine[0] = "add"; // Código 0
        commandLine[1] = "%R1";
        commandLine[2] = "%R2";
        ass.proccessCommand(commandLine);

        commandLine[0] = "sub"; // Código 5
        commandLine[1] = "mem1";
        commandLine[2] = "%R3";
        ass.proccessCommand(commandLine);

        commandLine[0] = "move"; // Código 9
        commandLine[1] = "%R2";
        commandLine[2] = "mem2";
        ass.proccessCommand(commandLine);

        commandLine[0] = "jmp"; // Código 13
        commandLine[1] = "label1";
        ass.proccessCommand(commandLine);

        commandLine[0] = "jn"; // Código 14
        commandLine[1] = "label2";
        ass.proccessCommand(commandLine);

        commandLine[0] = "jeq"; // Código 16
        commandLine[1] = "%R1";
        commandLine[2] = "%R2";
        commandLine[3] = "label3";
        ass.proccessCommand(commandLine);

        commandLine[0] = "call"; // Código 20
        commandLine[1] = "subroutine1";
        ass.proccessCommand(commandLine);

        commandLine[0] = "ret"; // Código 21
        ass.proccessCommand(commandLine);

	    returnedObj = ass.getObjProgram();
	    assertEquals(20, returnedObj.size()); // O programa de objeto deve ter 20 linhas

	 // Validando os códigos e parâmetros
        assertEquals("0", returnedObj.get(0)); // Código de add
        assertEquals("%R1", returnedObj.get(1)); // Parâmetro
        assertEquals("%R2", returnedObj.get(2)); // Parâmetro

        assertEquals("5", returnedObj.get(3)); // Código de sub
        assertEquals("&mem1", returnedObj.get(4)); // Parâmetro
        assertEquals("%R3", returnedObj.get(5)); // Parâmetro

        assertEquals("9", returnedObj.get(6)); // Código de move
        assertEquals("%R2", returnedObj.get(7)); // Parâmetro
        assertEquals("&mem2", returnedObj.get(8)); // Parâmetro

        assertEquals("13", returnedObj.get(9)); // Código de jmp
        assertEquals("&label1", returnedObj.get(10)); // Parâmetro

        assertEquals("14", returnedObj.get(11)); // Código de jn
        assertEquals("&label2", returnedObj.get(12)); // Parâmetro

        assertEquals("16", returnedObj.get(13)); // Código de jeq
        assertEquals("%R1", returnedObj.get(14)); // Parâmetro
        assertEquals("%R2", returnedObj.get(15)); // Parâmetro
        assertEquals("&label3", returnedObj.get(16)); // Parâmetro

        assertEquals("20", returnedObj.get(17)); // Código de call
        assertEquals("&subroutine1", returnedObj.get(18)); // Parâmetro

        assertEquals("21", returnedObj.get(19)); // Código de ret
	}


	@Test
	public void testParse() {
		Assembler ass = new Assembler();
		ArrayList<String> returnedObj = new ArrayList<>();
		ArrayList<String> sourceProgram = new ArrayList<>();
		
		 // Programa com os seguintes comandos:
	    /*
	     * var1
	     * var2
	     * var3
	     * move 10 %RPG0
	     * move %RPG0 var3
	     * move 2 %RPG0
	     * move %RPG0 var2
	     * move 0 %RPG0
	     * move %RPG0 var1
	     * label:
	     * move var1 %RPG0
	     * add %RPG0 %RPG1
	     * move %RPG0 var1
	     * move %RPG1 %RPG0
	     * sub var3 %RPG0
	     */
		sourceProgram.add("var1");
		sourceProgram.add("var2");
		sourceProgram.add("var3");
		sourceProgram.add("move 10 %RPG0");
		sourceProgram.add("move %RPG0 var3");
		sourceProgram.add("move 2 %RPG0");
		sourceProgram.add("move %RPG0 var2");
		sourceProgram.add("move 0 %RPG0");
		sourceProgram.add("move %RPG0 var1");
		sourceProgram.add("label:");
		sourceProgram.add("move var1 %RPG0");
		sourceProgram.add("add %RPG0 %RPG1");
		sourceProgram.add("move %RPG0 var1");
		sourceProgram.add("move %RPG1 %RPG0");
		sourceProgram.add("sub var3 %RPG0");
		sourceProgram.add("jn label");
		
		//now we can generate the object program
		ass.setLines(sourceProgram);
		ass.parse();
		returnedObj = ass.getObjProgram();
		System.out.println(returnedObj);
		
		//teste
		assertEquals(35, returnedObj.size()); // Verifica o tamanho do programa gerado
		// Checando linha por linha
		assertEquals("11", returnedObj.get(0));  // move 10 %RPG0 (load immediate)
		assertEquals("10", returnedObj.get(1));  // parâmetro do move
		assertEquals("%RPG0", returnedObj.get(2));  // destino do move

		assertEquals("9", returnedObj.get(3));  // move %RPG0 var3 (store)
		assertEquals("%RPG0", returnedObj.get(4));  // origem do move
		assertEquals("&var3", returnedObj.get(5));  // destino do store

		assertEquals("11", returnedObj.get(6));  // move 2 %RPG0 (load immediate)
		assertEquals("2", returnedObj.get(7));   // parâmetro do move
		assertEquals("%RPG0", returnedObj.get(8));  // destino do move

		assertEquals("9", returnedObj.get(9));  // move %RPG0 var2 (store)
		assertEquals("%RPG0", returnedObj.get(10)); // origem do move
		assertEquals("&var2", returnedObj.get(11)); // destino do store

		assertEquals("11", returnedObj.get(12));  // move 0 %RPG0 (load immediate)
		assertEquals("0", returnedObj.get(13));   // parâmetro do move
		assertEquals("%RPG0", returnedObj.get(14));  // destino do move

		assertEquals("9", returnedObj.get(15));  // move %RPG0 var1 (store)
		assertEquals("%RPG0", returnedObj.get(16)); // origem do move
		assertEquals("&var1", returnedObj.get(17)); // destino do store

		assertEquals("8", returnedObj.get(18));  // move var1 %RPG0 (load)
		assertEquals("&var1", returnedObj.get(19)); // origem do load
		assertEquals("%RPG0", returnedObj.get(20)); // destino do move

		assertEquals("0", returnedObj.get(21));  // add %RPG0 %RPG1
		assertEquals("%RPG0", returnedObj.get(22)); // primeiro parâmetro do add
		assertEquals("%RPG1", returnedObj.get(23)); // segundo parâmetro do add

		assertEquals("9", returnedObj.get(24));  // move %RPG0 var1 (store)
		assertEquals("%RPG0", returnedObj.get(25)); // origem do move
		assertEquals("&var1", returnedObj.get(26)); // destino do store

		assertEquals("10", returnedObj.get(27));  // move %RPG1 %RPG0
		assertEquals("%RPG1", returnedObj.get(28)); // origem do move
		assertEquals("%RPG0", returnedObj.get(29)); // destino do move

		assertEquals("5", returnedObj.get(30));  // sub var3 %RPG0
		assertEquals("&var3", returnedObj.get(31)); // origem do sub
		assertEquals("%RPG0", returnedObj.get(32)); // destino do sub

		assertEquals("14", returnedObj.get(33));  // jn label
		assertEquals("&label", returnedObj.get(34)); // parâmetro do jn
		
		
		//now, checking if the label "label" was inserted, pointing to the position 12
		//the line 'read var1' is just after the label
		//once the command was inserted in the position 12, the label must 
		//be pointing to the position 12
		assertTrue(ass.getLabels().contains("label"));
		assertEquals(1, ass.getLabels().size());
		assertEquals(1, ass.getLabelsAddresses().size());
		assertEquals(0, ass.getLabels().indexOf("label"));
		assertEquals(18, (int) ass.getLabelsAddresses().get(0));
		
		//checking if all variables are stored in variables collection
		assertEquals("var1", ass.getVariables().get(0));
		assertEquals("var2", ass.getVariables().get(1));
		assertEquals("var3", ass.getVariables().get(2));
	}

	@Test
	public void testReplaceVariable() {
		
		Assembler ass = new Assembler();
		ArrayList<String> sampleexec = new ArrayList<>();
		
		//creating a fictional exec program with some variables
		sampleexec.add("9");
		sampleexec.add("&var1"); //var1 in the position 1
		sampleexec.add("9");
		sampleexec.add("9");
		sampleexec.add("9");
		sampleexec.add("&var2"); //var2 in the position 5
		sampleexec.add("9");
		sampleexec.add("&var1"); //var1 in the position 7
		sampleexec.add("9");
		sampleexec.add("9");
		sampleexec.add("&var3"); //var3 in the position 10
		sampleexec.add("9");
		sampleexec.add("&var3"); //var3 in the position 12
		sampleexec.add("9");
		sampleexec.add("&var1"); //var1 in the position 14
		
		//inserting this arraylist into the execprogram collections
		ass.setExecProgram(sampleexec);
		
		//now the test!
		//var1 must be replaced by position 100
		//var2 must be replaced by  position 99
		//var2 must be replaced by  position 98
		
		ass.replaceVariable("var1", 100);
		ass.replaceVariable("var2", 99);
		ass.replaceVariable("var3", 98);
		
		//getting the positions 
		//var1 is now the address 100. It must be found in positions 1, 7 and 14 
		assertEquals("100", ass.getExecProgram().get(1));
		assertEquals("100", ass.getExecProgram().get(7));
		assertEquals("100", ass.getExecProgram().get(14));
		
		//var2 is now the address 99. It must be found in positions 5 
		assertEquals("99", ass.getExecProgram().get(5));
		
		//var3 is now the address 98. It must be found in positions 10 and 12
		assertEquals("98", ass.getExecProgram().get(10));
		assertEquals("98", ass.getExecProgram().get(12));
		
	}
	
	@Test
	public void testReplaceLabels() {
		Assembler ass = new Assembler();
		ArrayList<String> sampleexec = new ArrayList<>();
		
		//creating a fictional exec program with some labels
		sampleexec.add("9");
		sampleexec.add("&label1"); //label1 in the position 1
		sampleexec.add("9");
		sampleexec.add("9");
		sampleexec.add("9");
		sampleexec.add("&label2"); //label2 in the position 5
		sampleexec.add("9");
		sampleexec.add("&label1"); //label1 in the position 7
		sampleexec.add("9");
		sampleexec.add("9");
		sampleexec.add("&label3"); //label3 in the position 10
		sampleexec.add("9");
		sampleexec.add("&label3"); //label3 in the position 12
		sampleexec.add("9");
		sampleexec.add("&label1"); //label1 in the position 14

		//inserting the labels
		ass.getLabels().add("label1");
		ass.getLabels().add("label2");
		ass.getLabels().add("label3");

		//and inserting the label addresses
		ass.getLabelsAddresses().add(17); //label 1 means position 17
		ass.getLabelsAddresses().add(42); //label 1 means position 42
		ass.getLabelsAddresses().add(63); //label 1 means position 63
		
		
		//inserting this arraylist we made above into the execprogram collections
		ass.setExecProgram(sampleexec);
		
		
		//now the test!
		ass.replaceLabels();
		//label1 (now refering to position 17) in positions 10 and 12
		assertEquals("17", ass.getExecProgram().get(1));
		assertEquals("17", ass.getExecProgram().get(7));
		assertEquals("17", ass.getExecProgram().get(14));
		
		//label2 (now refering to position 42) in positions 5
		assertEquals("42", ass.getExecProgram().get(5));
		
		//label3 (now refering to position 63) in positions 1, 7 and 14
		assertEquals("63", ass.getExecProgram().get(10));
		assertEquals("63", ass.getExecProgram().get(12));
	}

	@Test
	public void testReplaceAllVariables() {
		Assembler ass = new Assembler();
		ArrayList<String> sampleexec = new ArrayList<>();
		
		//creating a fictional exec program with some variables
		sampleexec.add("9");
		sampleexec.add("&var1"); //var1 in the position 1
		sampleexec.add("9");
		sampleexec.add("9");
		sampleexec.add("9");
		sampleexec.add("&var2"); //var2 in the position 5
		sampleexec.add("9");
		sampleexec.add("&var1"); //var1 in the position 7
		sampleexec.add("9");
		sampleexec.add("9");
		sampleexec.add("&var3"); //var3 in the position 10
		sampleexec.add("9");
		sampleexec.add("&var3"); //var3 in the position 12
		sampleexec.add("9");
		sampleexec.add("&var1"); //var1 in the position 14
		
		//inserting this arraylist into the execprogram collections
		ass.setExecProgram(sampleexec);
		
		//now inserting variables
		ass.getVariables().add("var1");
		ass.getVariables().add("var2");
		ass.getVariables().add("var3");
		
		//in this architecture, the memory size is 256
		//so, var1 must be replaced by 255, var2 by 254 and var3 by 253
		ass.replaceAllVariables();
		//var1 (now 255) is in lines 5, 7 and 14
		assertEquals("255", ass.getExecProgram().get(1));
		assertEquals("255", ass.getExecProgram().get(7));
		assertEquals("255", ass.getExecProgram().get(14));
		
		//var2 (now 254) is in line 5
		assertEquals("254", ass.getExecProgram().get(5));
		
		//var3 (now 253) is in lines 10 and 12
		assertEquals("253", ass.getExecProgram().get(10));
		assertEquals("253", ass.getExecProgram().get(12));
	}
	
	@Test
	public void testReplaceRegisters() {
		
		Assembler ass = new Assembler();
		ArrayList<String> sampleexec = new ArrayList<>();
		
		//creating a fictional exec program with some registers
		sampleexec.add("9");
		sampleexec.add("%RPG1"); //rpg1 in the position 1
		sampleexec.add("9");
		sampleexec.add("9");
		sampleexec.add("%PC"); //pc is in position 4
		sampleexec.add("9");
		sampleexec.add("%RPG0"); //rpg0 is in position 6
		sampleexec.add("9");
		sampleexec.add("9");
		sampleexec.add("%IR"); //ir in the position 9
		sampleexec.add("9");
		sampleexec.add("%RPG0"); //rpg0 in the position 11
		sampleexec.add("9");
		sampleexec.add("%RPG2"); //rpg1 in the position 13
		sampleexec.add("9");
		sampleexec.add("%RPG3"); //rpg1 in the position 15
		sampleexec.add("9");
		sampleexec.add("%StkTOP"); //StkTop in the position 17
		sampleexec.add("9");
		sampleexec.add("%StkBOT"); //StkBot in the position 19
		
		//inserting this arraylist into the execprogram collections
		ass.setExecProgram(sampleexec);
		
		//now the test!
		//RPG0 must be replaced by 0 
		//RPG1 must be replaced by 1
		//RPG2 must be replaced by 2
		//RPG3 must be replaced by 3
		//StkTOP must be replaced by 4
		//StkBOT must be replaced by 5
		//PC must be replaced by 6
		//IR must be replaced by 7
		
		ass.replaceRegisters();
		
		//getting the positions 
		//rpg0 is now the number 0. It must be found in positions 6 and 11
		assertEquals("0", ass.getExecProgram().get(6));
		assertEquals("0", ass.getExecProgram().get(11));
		
		//rpg1 is now the number 1. It must be found in positions 1
		assertEquals("1", ass.getExecProgram().get(1));
		
		//rpg2 is now the number 2. It must be found in positions 13
		assertEquals("2", ass.getExecProgram().get(13));
		
		//rpg3 is now the number 3. It must be found in positions 15
		assertEquals("3", ass.getExecProgram().get(15));
		
		//StkTOP is now the number 4. It must be found in positions 17
		assertEquals("4", ass.getExecProgram().get(17));
				
		//StkBOT is now the number 5. It must be found in positions 19
		assertEquals("5", ass.getExecProgram().get(19));
		
		//pc is now the number 6. It must be found in position 4 
		assertEquals("6", ass.getExecProgram().get(4));

		//ir is now the number 7. It must be found in position 9 
		assertEquals("7", ass.getExecProgram().get(9));
		

		
	}
	
	@Test
	public void testCheckLabels() {
		Assembler ass = new Assembler();
		ArrayList<String> sampleexec = new ArrayList<>();
		
		//creating a fictional exec program with some variables
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("&label1"); 
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("&label2"); 
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("&var1"); 
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("&label3"); 
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("&label3"); 
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("&var1"); 
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("&label1"); 
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("&var2"); 
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("&var1"); 
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("&label3"); 
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("&var3"); 
		ass.getObjProgram().add("9");
		ass.getObjProgram().add("&label1"); 
		
		
		//case 1: some labels are not found
		
						//inserting the labels
						ass.getLabels().add("label1");
						ass.getLabels().add("label2");
						//label 3 is missing
						

								
						//now inserting variables
						ass.getVariables().add("var1");
						ass.getVariables().add("var2");
						ass.getVariables().add("var3");

		
						assertFalse(ass.checkLabels());

						
		//case 2: some variables are not found
						
						ass.getLabels().clear();
						ass.getVariables().clear();


						//inserting the labels
						ass.getLabels().add("label1");
						ass.getLabels().add("label2");
						ass.getLabels().add("label3");
				
								
						//now inserting variables
						ass.getVariables().add("var1");
						ass.getVariables().add("var2");
						//var3 is missing
		
						assertFalse(ass.checkLabels());
						
		//case 3: all variables and labels are found
						
						ass.getLabels().clear();
						ass.getVariables().clear();


						//inserting the labels
						ass.getLabels().add("label1");
						ass.getLabels().add("label2");
						ass.getLabels().add("label3");
				
								
						//now inserting variables
						ass.getVariables().add("var1");
						ass.getVariables().add("var2");
						ass.getVariables().add("var3");
		
						assertTrue(ass.checkLabels());
		
	}
	
	
	//@Test
	public void testRead() {
		fail("Not yet implemented");
	}
}
