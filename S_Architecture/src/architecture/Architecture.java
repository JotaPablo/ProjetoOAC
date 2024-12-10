package architecture;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import components.Bus;
import components.Demux;
import components.Memory;
import components.Register;
import components.Ula;

public class Architecture {
	
	private boolean simulation; //this boolean indicates if the execution is done in simulation mode.
								//simulation mode shows the components' status after each instruction
	
	
	private boolean halt;
	private Bus extbus1;
	private Bus intbus1;
	private Bus intbus2;
	private Memory memory;
	private Memory statusMemory;
	private int memorySize;
	private Register PC;
	private Register IR;
	private Register RPG;
	private Register RPG1;
	private Register RPG2;
	private Register RPG3;
	private Register StkTOP;
	private Register StkBOT;
	private Register Flags;
	private Ula ula;
	private Demux demux; //only for multiple register purposes
	
	private ArrayList<String> commandsList;
	private ArrayList<Register> registersList;
	
	

	/**
	 * Instanciates all components in this architecture
	 */
	private void componentsInstances() {
		//don't forget the instantiation order
		//buses -> registers -> ula -> memory
		extbus1 = new Bus();
		intbus1 = new Bus();
		intbus2 = new Bus();
		PC = new Register("PC", extbus1, intbus2);
		IR = new Register("IR", extbus1, intbus2);
		
		RPG = new Register("RPG0", intbus1, intbus2);
		RPG1 = new Register ("RPG1", intbus1, intbus2);
		RPG2 = new Register("RPG2", intbus1, intbus2);
		RPG3 = new Register("RPG3", intbus1, intbus2);
		
		StkTOP = new Register("StkTOP", extbus1, null); // Só se liga ao barramento externo 1
		StkBOT = new Register("StkBOT", extbus1, null); // Só se liga ao barramento externo 1

		Flags = new Register(2, intbus2);
		fillRegistersList();
		ula = new Ula(intbus1, intbus2);
		statusMemory = new Memory(2, extbus1); //Podemos mudar dependendo de como queiram fazer os jumps
		memorySize = 256;
		memory = new Memory(memorySize, extbus1);
		demux = new Demux(); //this bus is used only for multiple register operations
		
		fillCommandsList();
	}

	/**
	 * This method fills the registers list inserting into them all the registers we have.
	 * IMPORTANT!
	 * The first register to be inserted must be the default RPG
	 */
	private void fillRegistersList() {
		registersList = new ArrayList<Register>();
		registersList.add(RPG);
		registersList.add(RPG1);
		registersList.add(RPG2);
		registersList.add(RPG3);
		registersList.add(StkTOP);
		registersList.add(StkBOT);
		registersList.add(PC);
		registersList.add(IR);
		registersList.add(Flags);
	}

	/**
	 * Constructor that instanciates all components according the architecture diagram
	 */
	public Architecture() {
		componentsInstances();
		
		//by default, the execution method is never simulation mode
		simulation = false;
	}

	
	public Architecture(boolean sim) {
		componentsInstances();
		
		//in this constructor we can set the simoualtion mode on or off
		simulation = sim;
	}



	//getters
	
	protected Bus getExtbus1() {
		return extbus1;
	}

	protected Bus getIntbus1() {
		return intbus1;
	}

	protected Bus getIntbus2() {
		return intbus2;
	}

	protected Memory getMemory() {
		return memory;
	}

	protected Register getPC() {
		return PC;
	}

	protected Register getIR() {
		return IR;
	}

	protected Register getRPG() {
		return RPG;
	}

	protected Register getFlags() {
		return Flags;
	}

	protected Ula getUla() {
		return ula;
	}

	public ArrayList<String> getCommandsList() {
		return commandsList;
	}



	// Tabela de instruções, com base nos microprogramas especificados
	/*
	 * add %<regA> %<regB>        -> RegB <- RegA + RegB
	 * add <addr> %<regA>         -> rpg <- rpg + addr
	 * add %<regA> <addr>         -> addr <- rpg + regA
	 * add <imm> %<regA>          -> rpg <- regA + imm, imm precisa ser um inteiro 
	 * sub <addr>                 -> rpg <- rpg - addr
	 * jmp <mem>                 -> pc <- mem
	 * jn <mem>                  -> se bitneg, pc <- mem
     * jz <mem>                  -> se bitZero, pc <- mem
     * jeq %<regA> %<regB>       -> se regA == regB, pc <- mem
	 * read <addr>                -> rpg <- addr
	 * store <addr>               -> addr <- rpg
	 * ldi <x>                    -> rpg <- x
	 * inc                        -> rpg++
	 * move %<regA> %<regB>       -> regA <- regB
	 */
	
	/**
	 * This method fills the commands list arraylist with all commands used in this architecture
	 */
	protected void fillCommandsList() {
		commandsList = new ArrayList<String>();
		commandsList.add("addRegReg"); //0
		commandsList.add("addMemReg"); //1
		commandsList.add("addRegMem"); //2
		commandsList.add("addImmReg"); //3
		commandsList.add("subRegReg"); //4
		commandsList.add("subMemReg"); //5
		commandsList.add("subRegMem"); //6
		commandsList.add("subImmReg"); //7
		commandsList.add("jmp");   //8
		commandsList.add("jn");    //9
		commandsList.add("jz");    //10
        commandsList.add("jeq");   //11
		commandsList.add("read");  //12
		commandsList.add("store"); //13
		commandsList.add("ldi");   //14
		commandsList.add("inc");   //15	
		commandsList.add("moveRegReg"); //16
	}

	
	/**
	 * This method is used after some ULA operations, setting the flags bits according the result.
	 * @param result is the result of the operation
	 * NOT TESTED!!!!!!!
	 */
	private void setStatusFlags(int result) {
		Flags.setBit(0, 0);
		Flags.setBit(1, 0);
		if (result==0) { //bit 0 in flags must be 1 in this case
			Flags.setBit(0,1);
		}
		if (result<0) { //bit 1 in flags must be 1 in this case
			Flags.setBit(1,1);
		}
	}
	
	/**
	 * Este método implementa o microprograma para:
	 *                 ADD <regA> <regB> // <regB> <- <regA> + <regB> 
	 * No código de máquina, este comando tem o número 0.
	 * 
	 * O método lê os dois IDs dos registradores (<regA> e <regB>) da memória, nas posições logo após o comando, e
	 * realiza uma adição com o valor do registrador <regA> e o valor do registrador <regB>.
	 *    
	 * O resultado final deve estar em regB.
	 * 
	 * 1. pc -> intbus2 // pc.internalRead()
	 * 2. ula <- intbus2 // ula.internalStore(1)
	 * 3. ula incs // ula.inc()
	 * 4. ula -> intbus2 // ula.internalRead(1)
	 * 5. pc <- intbus2 // pc.internalStore() agora pc aponta para o primeiro parâmetro (o primeiro reg ID)
	 * 6. pc -> extbus // pc.read() agora o endereço onde está a posição a ser lida está no barramento externo
	 * 7. memória lê do extbus // isso força a memória a escrever o parâmetro (primeiro regID) no extbus
	 * 8. pc -> intbus2 // pc.internalRead()
	 * 9. ula <- intbus2 // ula.internalStore(1)
	 * 10. ula incs // ula.inc()
	 * 11. ula -> intbus2 // ula.internalRead(1)
	 * 12. pc <- intbus2 // pc.internalStore() agora pc aponta para o segundo parâmetro (o segundo reg ID)
	 * 13. demux <- extbus // demux.setValue(extbus1.get()) aponta para o registrador correto
	 * 14. registradores -> intbus2 // registersInternalRead() começa a leitura do registrador identificado no barramento demux
	 * 15. ula <- intbus2 // ula.internalStore(0)
	 * 16. pc -> extbus // pc.read()
	 * 17. memória lê do extbus // o segundo ID de registrador agora está no extbus
	 * 18. demux <- extbus // demux.setValue(extbus1.get()) aponta para o registrador correto
	 * 19. registradores -> intbus2 // registersInternalRead() realiza uma leitura interna do registrador identificado no barramento demux
	 * 20. ula <- intbus2 // ula.internalStore(1)
	 * 21. ula soma // ula.add()
	 * 22. ula -> intbus2 // ula.internalRead(1)
	 * 23. mudarFlags // setStatusFlags(intbus2.get()) altera os flags devido ao fim da operação
	 * 24. registradores <- intbus2 // registersInternalStore()
	 * 25. pc -> intbus2 // pc.internalRead()
	 * 26. ula <- intbus2 // ula.internalStore(1)
	 * 27. ula incs // ula.inc()
	 * 28. ula -> intbus2 // ula.internalRead(1)
	 * 29. pc <- intbus2 // pc.internalStore() agora pc aponta para a próxima instrução
	 * Fim
	 */
	public void addRegReg() {
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore(); // agora o PC aponta para o primeiro parâmetro (o primeiro reg ID)
	    PC.read();
	    memory.read(); // o primeiro ID de registrador está agora no barramento externo.
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore(); // agora o PC aponta para o segundo parâmetro (o segundo reg ID)
	    demux.setValue(extbus1.get()); // aponta para o registrador correto
	    registersInternalRead(); // começa a leitura do registrador identificado no barramento demux
	    ula.internalStore(0);
	    PC.read();
	    memory.read(); // o segundo ID de registrador está agora no barramento externo.
	    demux.setValue(extbus1.get()); // aponta para o registrador correto
	    registersInternalRead(); // realiza uma leitura interna do registrador identificado no barramento demux
	    ula.internalStore(1);
	    ula.add();
	    ula.internalRead(1);
	    setStatusFlags(intbus2.get()); // altera os flags devido ao fim da operação
	    registersInternalStore();
	    PC.internalRead(); // precisamos fazer com que o PC aponte para o próximo endereço de instrução
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore(); // agora o PC aponta para a próxima instrução. Voltamos para o estado FETCH.
	}
	
	
	
	/**
	 * Este método implementa o microprograma para
	 *                    ADD <mem> <regA> // <regA> <- <mem> + <regA>
	 * No código de máquina, o número desse comando é 1
	 * 
	 * O método lê o valor da memória (endereço da posição) e o id do registrador (<regA>), nas posições logo após 
	 * o comando, e realiza uma soma com esses valores.
	 *     
	 * O resultado final deve ficar em regA.
	 * 
	 * 1. pc -> intbus2 // pc.internalRead()
	 * 2. ula <- intbus2 // ula.internalStore(1)
	 * 3. ula incs // ula.inc()
	 * 4. ula -> intbus2 // ula.internalRead(1)
	 * 5. pc <- intbus2 // pc.internalStore() agora o pc aponta para o primeiro parâmetro (a posição de memória)
	 * 6. pc -> extbus // pc.read() o endereço da posição a ser lida agora está no barramento externo
	 * 7. memória lê do extbus // a memória escreve os dados na posição para o extbus
	 * 8. memória lê do extbus // a memória escreve o valor dos dados para o extbus
	 * 9. ir <- extbus // ir.store() o valor dos dados agora está no ir
	 * 10. pc -> intbus2 // pc.internalRead()
	 * 11. ula <- intbus2 // ula.internalStore(1)
	 * 12. ula incs // ula.inc()
	 * 13. ula -> intbus2 // ula.internalRead(1)
	 * 14. pc <- intbus2 // pc.internalStore() agora o pc aponta para o segundo parâmetro (o id do registrador)
	 * 15. pc -> extbus // pc.read()
	 * 16. memória lê do extbus // o id do registrador agora está no extbus
	 * 17. demux <- extbus // demux.setValue(extbus1.get()) aponta para o registrador correto
	 * 18. registradores -> intbus2 // registersInternalRead() realiza o armazenamento interno para o registrador identificado no barramento demux
	 * 19. ula <- intbus2 // ula.internalStore(1)
	 * 20. ir -> intbus2 // ir.internalRead()
	 * 21. ula <- intbus2 // ula.internalStore(0)
	 * 22. ula soma // ula.add()
	 * 23. ula -> intbus2 // ula.internalRead(1)
	 * 24. changeFlags // setStatusFlags(intbus2.get()) alterando os flags devido ao fim da operação
	 * 25. registradores <- intbus2 // registersInternalStore()
	 * 26. pc -> intbus2 // pc.internalRead()
	 * 27. ula <- intbus2 // ula.internalStore(1)
	 * 28. ula incs // ula.inc()
	 * 29. ula -> intbus2 // ula.internalRead(1)
	 * 30. pc <- intbus2 // pc.internalStore() agora o pc aponta para a próxima instrução
	 * fim
	 */
	 
	public void addMemReg() {
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	    PC.read();
	    memory.read();
	    memory.read();
	    IR.store();
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	    PC.read();
	    memory.read();
	    demux.setValue(extbus1.get());
	    registersInternalRead();
	    ula.internalStore(1);
	    IR.internalRead();
	    ula.internalStore(0);
	    ula.add();
	    ula.internalRead(1);
	    setStatusFlags(intbus2.get());
	    registersInternalStore();
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	}

	/**
	 * Este método implementa o microprograma para
	 *                    ADD <regA> <mem> // <mem> <- <mem> + <regA>
	 * No código de máquina, o número desse comando é 2
	 * 
	 * O método lê o valor da memória (endereço da posição) e o id do registrador (<regA>), nas posições logo após 
	 * o comando, e realiza uma soma com esses valores.
	 *     
	 * O resultado final deve ficar em <mem>.
	 * 
	 * 1. ula <- intbus2            // ula.internalStore(1);
	 * 2. ula incs                  // ula.inc();
	 * 3. ula -> intbus2            // ula.internalRead(1);
	 * 4. pc <- intbus2             // PC.internalStore(); agora o pc aponta para o primeiro parâmetro (o id do reg)
	 * 5. pc -> extbus              // PC.read();
	 * 6. memória lê do extbus      // memory.read(); o id do registrador agora está no extbus
	 * 7. demux <- extbus           // demux.setValue(extbus1.get()); aponta para o registrador correto
	 * 8. pc -> intbus2            // PC.internalRead(); 
	 * 9. ula <- intbus2           // ula.internalStore(1);
	 * 10. ula incs                 // ula.inc();
	 * 11. ula -> intbus2           // ula.internalRead(1);
	 * 12. pc <- intbus2            // PC.internalStore(); agora o pc aponta para o segundo parâmetro (a posição de memória)
	 * 13. pc -> extbus             // PC.read();
	 * 14. memória lê do extbus     // memory.read();  memória escreve a posição de dados para o extbus
	 * 15. memória lê do extbus     // memory.read(); memória escreve o valor dos dados para o extbus
	 * 16. ir <- extbus             // IR.store(); o valor dos dados agora está no ir
	 * 17. ir -> intbus2            // IR.internalRead();
	 * 18. ula <- intbus2           // ula.internalStore(1);
	 * 19. registradores -> intbus2 // registersInternalRead();
	 * 20. ula <- intbus2           // ula.internalStore(0);
	 * 21. ula soma                 // ula.add();
	 * 22. ula -> intbus2           // ula.internalRead(1);
	 * 23. changeFlags              // setStatusFlags(intbus2.get());
	 * 24. registradores <- intbus2 // IR.internalStore(); alterando os flags devido ao fim da operação
	 * 25. pc -> intbus2            // PC.read(); 
	 * 26. memória lê do extbus     // memory.read(); memória escreve a posição de dados para o extbus
	 * 27. memória armazena no extbus // memory.store(); memória lê o endereço e aguarda o valor
	 * 28. ir -> extbus             // IR.read();
	 * 29. memória escreve valor no extbus // memory.store(); o valor dos dados agora está armazenado
	 * 30. pc -> intbus2            // PC.internalRead();
	 * 31. ula <- intbus2           // ula.internalStore(1);
	 * 32. ula incs                 // ula.inc();
	 * 33. ula -> intbus2           // ula.internalRead(1);
	 * 34. pc <- intbus2            // PC.internalStore(); agora o pc aponta para a próxima instrução
	 * fim
	 */
	 
	public void addRegMem() {
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	    PC.read();
	    memory.read();
	    demux.setValue(extbus1.get());
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	    PC.read();
	    memory.read();
	    memory.read();
	    IR.store();
	    IR.internalRead();
	    ula.internalStore(1);
	    registersInternalRead();
	    ula.internalStore(0);
	    ula.add();
	    ula.internalRead(1);
	    setStatusFlags(intbus2.get());
	    IR.internalStore();
	    PC.read();
	    memory.read();
	    memory.store();
	    IR.read();
	    memory.store();
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	}

	/**
	 * Este método implementa o microprograma para
	 *                    ADD <imm> <regA> // <regA> <- <imm> + <regA>
	 * No código de máquina, o número desse comando é 3
	 * 
	 * O método lê o valor da posição logo após o comando e o id do registrador (<regA>), nas posições logo após 
	 * o comando, e realiza uma soma com esses valores.
	 *     
	 * O resultado final deve ficar em regA.
	 * 
	 * 1. pc -> intbus2 // pc.internalRead()
	 * 2. ula <- intbus2 // ula.internalStore(1)
	 * 3. ula incs // ula.inc()
	 * 4. ula -> intbus2 // ula.internalRead(1)
	 * 5. pc <- intbus2 // pc.internalStore() agora o pc aponta para o primeiro parâmetro (a posição de memória)
	 * 6. pc -> extbus // pc.read() o endereço onde a posição será lida está agora no barramento externo
	 * 7. memória lê do extbus // memória escreve o valor dos dados para o extbus
	 * 9. ir <- extbus // ir.store() o valor dos dados agora está no ir
	 * 10. pc -> intbus2 // pc.internalRead()
	 * 11. ula <- intbus2 // ula.internalStore(1)
	 * 12. ula incs // ula.inc()
	 * 13. ula -> intbus2 // ula.internalRead(1)
	 * 14. pc <- intbus2 // pc.internalStore() agora o pc aponta para o segundo parâmetro (o id do registrador)
	 * 15. pc -> extbus // pc.read()
	 * 16. memória lê do extbus // o id do registrador agora está no extbus
	 * 17. demux <- extbus // demux.setValue(extbus1.get()) aponta para o registrador correto
	 * 18. registradores -> intbus2 // registersInternalRead() realiza o armazenamento interno para o registrador identificado no barramento demux
	 * 19. ula <- intbus2 // ula.internalStore(1)
	 * 20. ir -> intbus2 // ir.internalRead()
	 * 21. ula <- intbus2 // ula.internalStore(0)
	 * 22. ula soma // ula.add()
	 * 23. ula -> intbus2 // ula.internalRead(1)
	 * 24. changeFlags // setStatusFlags(intbus2.get()) alterando os flags devido ao fim da operação
	 * 25. registradores <- intbus2 // registersInternalStore()
	 * 26. pc -> intbus2 // pc.internalRead()
	 * 27. ula <- intbus2 // ula.internalStore(1)
	 * 28. ula incs // ula.inc()
	 * 29. ula -> intbus2 // ula.internalRead(1)
	 * 30. pc <- intbus2 // pc.internalStore() agora o pc aponta para a próxima instrução
	 * fim
	 */
	 
	public void addImmReg() {
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	    PC.read();
	    memory.read();
	    IR.store();
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	    PC.read();
	    memory.read();
	    demux.setValue(extbus1.get());
	    registersInternalRead();
	    ula.internalStore(1);
	    IR.internalRead();
	    ula.internalStore(0);
	    ula.add();
	    ula.internalRead(1);
	    setStatusFlags(intbus2.get());
	    registersInternalStore();
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	}

		

	/**
	 * This method implements the microprogram for
	 * 					SUB address
	 * In the machine language this command number is 1, and the address is in the position next to him
	 *    
	 * where address is a valid position in this memory architecture
	 * The method reads the value from memory (position address) and 
	 * performs an SUB with this value and that one stored in the rpg (the first register in the register list).
	 * The final result must be in RPG (the first register in the register list).
	 * The logic is
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <-  intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.store() now pc points to the parameter
	 * 6. rpg -> intbus1 //rpg.read() the current rpg value must go to the ula 
	 * 7. ula <- intbus1 //ula.store()
	 * 8. pc -> extbus (pc.read())
	 * 9. memory reads from extbus //this forces memory to write the data position in the extbus
	 * 10. memory reads from extbus //this forces memory to write the data value in the extbus
	 * 11. rpg <- extbus (rpg.store())
	 * 12. rpg -> intbus1 (rpg.read())
	 * 13. ula  <- intbus1 //ula.store()
	 * 14. Flags <- zero //the status flags are reset
	 * 15. ula subs
	 * 16. ula -> intbus1 //ula.read()
	 * 17. ChangeFlags //informations about flags are set according the result 
	 * 18. rpg <- intbus1 //rpg.store() - the add is complete.
	 * 19. pc -> intbus2 //pc.read() now pc must point the next instruction address
	 * 20. ula <- intbus2 //ula.store()
	 * 21. ula incs
	 * 22. ula -> intbus2 //ula.read()
	 * 23. pc <- intbus2 //pc.store() 
	 * end
	 * @param address
	 */
	
	public void subRegReg() {
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore(); // agora o PC aponta para o primeiro parâmetro (o primeiro reg ID)
	    PC.read();
	    memory.read(); // o primeiro ID de registrador está agora no barramento externo.
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore(); // agora o PC aponta para o segundo parâmetro (o segundo reg ID)
	    demux.setValue(extbus1.get()); // aponta para o registrador correto
	    registersInternalRead(); // começa a leitura do registrador identificado no barramento demux
	    ula.internalStore(0);
	    PC.read();
	    memory.read(); // o segundo ID de registrador está agora no barramento externo.
	    demux.setValue(extbus1.get()); // aponta para o registrador correto
	    registersInternalRead(); // realiza uma leitura interna do registrador identificado no barramento demux
	    ula.internalStore(1);
	    ula.sub();
	    ula.internalRead(1);
	    setStatusFlags(intbus2.get()); // altera os flags devido ao fim da operação
	    registersInternalStore();
	    PC.internalRead(); // precisamos fazer com que o PC aponte para o próximo endereço de instrução
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore(); // agora o PC aponta para a próxima instrução. Voltamos para o estado FETCH.
	}
	
	

	 
	public void subMemReg() {
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	    PC.read();
	    memory.read();
	    memory.read();
	    IR.store();
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	    PC.read();
	    memory.read();
	    demux.setValue(extbus1.get());
	    registersInternalRead();
	    ula.internalStore(1);
	    IR.internalRead();
	    ula.internalStore(0);
	    ula.sub();
	    ula.internalRead(1);
	    setStatusFlags(intbus2.get());
	    registersInternalStore();
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	}


	public void subRegMem() {
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	    PC.read();
	    memory.read();
	    demux.setValue(extbus1.get());
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	    PC.read();
	    memory.read();
	    memory.read();
	    IR.store();
	    IR.internalRead();
	    ula.internalStore(1);
	    registersInternalRead();
	    ula.internalStore(0);
	    ula.sub();
	    ula.internalRead(1);
	    setStatusFlags(intbus2.get());
	    IR.internalStore();
	    PC.read();
	    memory.read();
	    memory.store();
	    IR.read();
	    memory.store();
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	}


	public void subImmReg() {
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	    PC.read();
	    memory.read();
	    IR.store();
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	    PC.read();
	    memory.read();
	    demux.setValue(extbus1.get());
	    registersInternalRead();
	    ula.internalStore(1);
	    IR.internalRead();
	    ula.internalStore(0);
	    ula.sub();
	    ula.internalRead(1);
	    setStatusFlags(intbus2.get());
	    registersInternalStore();
	    PC.internalRead();
	    ula.internalStore(1);
	    ula.inc();
	    ula.internalRead(1);
	    PC.internalStore();
	}
	public void sub() {
		PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore(); //now PC points to the parameter address
		RPG.internalRead();
		ula.store(0); //the rpg value is in ULA (0). This is the first parameter
		PC.read(); 
		memory.read(); // the parameter is now in the external bus. 
						//but the parameter is an address and we need the value
		memory.read(); //now the value is in the external bus
		RPG.store();
		RPG.internalRead();
		ula.store(1); //the rpg value is in ULA (0). This is the second parameter
		ula.sub(); //the result is in the second ula's internal register
		ula.internalRead(1);; //the operation result is in the internalbus 2
		setStatusFlags(intbus2.get()); //changing flags due the end of the operation
		RPG.internalStore(); //now the sub is complete
		PC.internalRead(); //we need to make PC points to the next instruction address
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore(); //now PC points to the next instruction. We go back to the FETCH status.
	}
	
        /**
     * Este método implementa o microprograma para
     *                      jmp mem
     * No código de máquina, este comando possui um número específico, e seu parâmetro está na posição seguinte:
     * - O parâmetro é o local de memoria onde está o endereço para onde será feito o salto.
     * 
     * O método lê o endereço que está no local de memoria e redireciona o PC para este endereço, realizando um salto incondicional.
     * 
     * A lógica é:
     * 1. pc -> intbus2                            // PC.internalRead()
     * 2. ula <- intbus2                           // ula.internalStore(1)
     * 3. ula inc                                  // ula.inc()
     * 4. ula -> intbus2                           // ula.internalRead(1)
     * 5. pc <- intbus2                            // PC.internalStore() (PC aponta para o endereço de parâmetro)
     * 6. pc -> extbus1                             // PC.read() coloca o endereço de parâmetro no extbus1
     * 7. memory.read()                             // memory.read() retorna endereço do local da memoria onde está contido o endereço de salto)
     * 8. memory.read()                             // memory.read() retorna o endereço de salto do extbus
     * 9. PC.store()                                // PC.store() atualiza o PC com o endereço de salto
     * fim
     * 
     *@param memory
    */
	public void jmp() {
		PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore(); 
		PC.read();
		memory.read();
		memory.read();
		PC.store();
	}
	
	/**
     * Este método implementa o microprograma para
     *                      jz mem
     * No código de máquina, este comando tem um número específico e seu parâmetro está na posição seguinte:
     * - O parâmetro é o local de memoria onde está contido o endereço  para onde será feito o salto caso o bit zero esteja setado.
     * 
     * O método lê o endereço que está no local de memória e redireciona o PC para este endereço caso o bit zero esteja setado. Caso contrário, o PC é atualizado para a próxima instrução.
     * 
     * A lógica é:
     * 1. pc -> intbus2                            // PC.internalRead()
     * 2. ula <- intbus2                           // ula.internalStore(1)
     * 3. ula inc                                  // ula.inc()
     * 4. ula -> intbus2                           // ula.internalRead(1)
     * 5. pc <- intbus2                            // PC.internalStore() (PC aponta para o parâmetro)
     * 6. pc -> extbus                             // PC.read()
     * 7. memória -> extbus                        // memory.read() (retorna o endereço do local da memoria onde está contido o endereço de salto))
     * 9. memoria -> extbus                        // memory.read() (retorna o endereço de salto no extbus)
     * 10. statusMemory(1) <- extbus                // statusMemory.storeIn1()
     * 11. ula inc                                  // ula.inc()
     * 12. ula -> intbus2                          // ula.internalRead(1)
     * 13. pc <- intbus2                           // PC.internalStore() (PC aponta para a próxima instrução)
     * 14. pc -> extbus                            // PC.read()
     * 15. statusMemory(0) <- extbus               // statusMemory.storeIn0()
     * 16. extbus <- flags(bitZERO)            // extbus1.put(flags.getBit(1))
     * 17. statusMemory -> extbus                  // statusMemory.read() (seleciona endereço com base no bit NEGATIVE)
     * 18. pc <- extbus                            // PC.store() (atualiza o PC com o endereço selecionado)
     * fim
	 * @param memory
	 */
	public void jz() {
		PC.internalRead();
	        ula.internalStore(1);
	        ula.inc();
	        ula.internalRead(1);
	        PC.internalStore();
	        PC.read();
	        memory.read();
	        memory.read();
	        statusMemory.storeIn1();
	        ula.inc();
	        ula.internalRead(1);
	        PC.internalStore();
	        PC.read();
	        statusMemory.storeIn0();
	        extbus1.put(Flags.getBit(0));
	        statusMemory.read();
	        PC.store();
	}
	
    /**
     * Este método implementa o microprograma para
     *                      jn mem
     * No código de máquina, este comando tem um número específico e seu parâmetro está na posição seguinte:
     * - O parâmetro é o endereço de memória para onde será feito o salto caso o bit NEGATIVE esteja setado.
     O método lê o endereço que está no local de memória, e redireciona o PC para este endereço caso o bit NEGATIVE esteja setado. Caso contrário, o PC é atualizado para a próxima instrução.
     * 
     * A lógica é:
     * 1. pc -> intbus2                            // PC.internalRead()
     * 2. ula <- intbus2                           // ula.internalStore(1)
     * 3. ula inc                                  // ula.inc()
     * 4. ula -> intbus2                           // ula.internalRead(1)
     * 5. pc <- intbus2                            // PC.internalStore() (PC aponta para o parâmetro)
     * 6. pc -> extbus                             // PC.read()
     * 7. memória -> extbus                        // memory.read() (retorna o endereço do local da memoria onde está contido o endereço de salto))
     * 8. memória -> extbus                        // memory.read() (retorna o endereço de salto no extbus)
     * 8. statusMemory(1) <- extbus                // statusMemory.storeIn1()
     * 9. ula inc                                  // ula.inc()
     * 10. ula -> intbus2                          // ula.internalRead(1)
     * 11. pc <- intbus2                           // PC.internalStore() (PC aponta para a próxima instrução)
     * 12. pc -> extbus                            // PC.read()
     * 13. statusMemory(0) <- extbus               // statusMemory.storeIn0()
     * 14. extbus <- flags(bitNEGATIVE)            // extbus1.put(flags.getBit(1))
     * 15. statusMemory -> extbus                  // statusMemory.read() (seleciona endereço com base no bit NEGATIVE)
     * 16. pc <- extbus                            // PC.store() (atualiza o PC com o endereço selecionado)
     * fim
	 * @param memory
	 */
	public void jn() {
	        PC.internalRead();
	        ula.internalStore(1);
	        ula.inc();
	        ula.internalRead(1);
	        PC.internalStore();
	        PC.read();
	        memory.read();
	        memory.read();
	        statusMemory.storeIn1();
	        ula.inc();
	        ula.internalRead(1);
	        PC.internalStore();
	        PC.read();
	        statusMemory.storeIn0();
	        extbus1.put(Flags.getBit(1));
	        statusMemory.read();
	        PC.store();
	}

    /**
 * Este método implementa o microprograma para
 *                      jeq reg reg mem
 * No código de máquina, este comando possui um número específico (a ser definido), e seus parâmetros estão nas posições seguintes:
 * - O primeiro parâmetro é o ID do primeiro registrador a ser comparado.
 * - O segundo parâmetro é o ID do segundo registrador a ser comparado.
 * - O terceiro parâmetro é o endereço de memória para onde será feito o salto caso os registradores sejam iguais.
 * 
 * O método lê os IDs dos dois registradores da memória, nas posições logo após o comando, e realiza uma comparação entre os valores contidos nesses registradores.
 * Se os valores forem iguais (flag ZERO setado), o programa salta para o endereço contido no local de memoria especificado. Caso contrário, continua para a próxima instrução.
 * 
 * A lógica é:
 * 1. pc -> intbus2                            // PC.internalRead()
 * 2. ula <- intbus2                           // ula.internalStore(1)
 * 3. ula inc                                  // ula.inc()
 * 4. ula -> intbus2                           // ula.internalRead(1)
 * 5. pc <- intbus2                            // PC.internalStore() (PC aponta para o primeiro parâmetro)
 * 6. pc -> extbus                             // PC.read()
 * 7. memória -> extbus                        // memory.read() (ID do primeiro registrador no extbus)
 * 8. demux <- extbus                          // demux.setValue(extbus1.get())
 * 9. registradores -> intbus2                 // registersInternalRead()
 * 10. ula <- intbus2                          // ula.internalStore(0) (valor armazenado em ULA[0])

 * 11. ula inc                                 // ula.inc()
 * 12. ula -> intbus2                          // ula.internalRead(1)
 * 13. pc <- intbus2                           // PC.internalStore() (PC aponta para o segundo parâmetro)
 * 14. pc -> extbus                            // PC.read()
 * 15. memória -> extbus                       // memory.read() (ID do segundo registrador no extbus)
 * 16. demux <- extbus                         // demux.setValue(extbus1.get())
 * 17. registradores -> intbus2                // registersInternalRead()
 * 18. ula <- intbus2                          // ula.internalStore(1) (valor armazenado em ULA[1])

 * 19. ula sub                                 // ula.sub() (ULA[0] - ULA[1])
 * 20. ula -> intbus2                          // ula.internalRead(1)
 * 21. alterar flags                           // setStatusFlags(intbus2.get())

 * 22. pc -> intbus2                           // PC.internalRead()
 * 23. ula <- intbus2                          // ula.internalStore(1)
 * 24. ula inc                                 // ula.inc()
 * 25. ula -> intbus2                          // ula.internalRead(1)
 * 26. pc <- intbus2                           // PC.internalStore() (PC aponta para o terceiro parâmetro)
 * 27. pc -> extbus                            // PC.read()
 * 28. memória -> extbus                       // memory.read() (retorna o local da memoria onde está contido o endereço de salto))
 * 29. memória -> extbus                       // memory.read() (retorna o endereço de salto no extbus)
 * 30. statusMemory(1) <- extbus               // statusMemory.storeIn1()

 * 31. ula inc                                 // ula.inc()
 * 32. ula -> intbus2                          // ula.internalRead(1)
 * 33. pc <- intbus2                           // PC.internalStore() (PC aponta para a próxima instrução)
 * 34. pc -> extbus                            // PC.read()
 * 35. statusMemory(0) <- extbus               // statusMemory.storeIn0()

 * 36. extbus <- flags(bitZERO)                // extbus1.put(flags.getBit(0))
 * 37. statusMemory -> extbus                  // statusMemory.read() (seleciona endereço com base no flag ZERO)
 * 38. pc <- extbus                            // PC.store() (atualiza o PC com o endereço selecionado)
 * fim
 *@param registerA
 *@param registerB
 *@param memory
 */
    public void jeq(){
        //leitura do primeiro registrador
        PC.internalRead(); //agora PC está no intbus2
        ula.internalStore(1); // ula grava no reg 1 o valor obtido de intbus2
        ula.inc();              // ula incrementa o valor
        ula.internalRead(1); // ula joga o valor para intbus2
        PC.internalStore();      // PC armazena o valor de intbus2
        PC.read();        // PC jogar o valor para o extbus
        memory.read();  // memory lê o valor do extbus e joga o id do registrador para o extbus
        demux.setValue(extbus1.get()); // demux aponta para o registrador correto
        registersInternalRead(); // lê o valor do registrador apontado pelo demux e retorna o valor armazenado para o intbus2
        ula.internalStore(0);   // ula armazena o valor de intbus2 no registrador 0

        //leitura do segundo registrador
        ula.inc(); // ula incrementa o endereço já presente em reg1
        ula.internalRead(1); // ula joga o valor para intbus2
        PC.internalStore(); // PC armazena o valor de intbus2
        PC.read(); // PC joga o valor para o extbus
        memory.read(); // memory lê o valor do extbus e joga o id do registrador para o extbus
        demux.setValue(extbus1.get()); // demux aponta para o registrador correto   
        registersInternalRead(); // lê o valor do registrador apontado pelo demux e retorna o valor armazenado para o intbus2
        ula.internalStore(1); // ula armazena o valor de intbus2 no registrador 1

        //comparação dos registradores
        ula.sub(); // ula subtrai os valores dos registradores 0 e 1
        ula.internalRead(1); // ula joga o valor para intbus2
        setStatusFlags(intbus2.get()); // altera os flags devido ao fim da operação

        //Prepara para a leitura do endereço de salto
        PC.internalRead(); // PC joga o valor no intbus2
        ula.internalStore(1); // ula armazena o valor de intbus2 no registrador 1
        ula.inc(); // ula incrementa o valor
        ula.internalRead(1); // ula joga o valor para intbus2
        PC.internalStore(); // PC armazena o valor de intbus2
        PC.read(); // PC joga o valor para o extbus
        memory.read(); // memory lê o valor do extbus e joga o endereço de memória para o extbus
        memory.read(); // memory lê o valor do extbus e joga o endereço de salto para o extbus
        statusMemory.storeIn1(); // statusMemory armazena o valor de extbus1
        ula.inc(); // ula incrementa o valor
        ula.internalRead(1); // ula joga o valor para intbus2
        PC.internalStore(); // PC armazena o valor de intbus2
        PC.read(); // PC joga o valor para o extbus 
        statusMemory.storeIn0(); // statusMemory armazena o valor de extbus1
        extbus1.put(Flags.getBit(0)); // extbus1 armazena o valor do bit zero

        //Decide o endereço de salto
        statusMemory.read(); // statusMemory lê o valor de extbus1
        PC.store(); // PC armazena o valor de extbus1
    }

	/**
	 * This method implements the microprogram for
	 * 					read address
	 * In the machine language this command number is 5, and the address is in the position next to him
	 *    
	 * where address is a valid position in this memory architecture 
	 * The method reads the value from memory (position address) and 
	 * inserts it into the RPG register (the first register in the register list)
	 * The logic is
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <-  intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.store() now pc points to the parameter
	 * 6. pc -> extbus //(pc.read())the address where is the position to be read is now in the external bus 
	 * 7. memory reads from extbus //this forces memory to write the address in the extbus
	 * 8. memory reads from extbus //this forces memory to write the stored data in the extbus
	 * 9. RPG <- extbus //the data is read
	 * 10. pc -> intbus2 //pc.read() now pc must point the next instruction address
	 * 11. ula <- intbus2 //ula.store()
	 * 12. ula incs
	 * 13. ula -> intbus2 //ula.read()
	 * 14. pc <- intbus2 //pc.store() 
	 * end
	 * @param address
	 */
	public void read() {
		PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore(); //now PC points to the parameter address
		PC.read(); 
		memory.read(); // the address is now in the external bus.
		memory.read(); // the data is now in the external bus.
		RPG.store();
		PC.internalRead(); //we need to make PC points to the next instruction address
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore(); //now PC points to the next instruction. We go back to the FETCH status.
	}
	
	/**
	 * This method implements the microprogram for
	 * 					store address
	 * In the machine language this command number is 6, and the address is in the position next to him
	 *    
	 * where address is a valid position in this memory architecture 
	 * The method reads the value from RPG (the first register in the register list) and 
	 * inserts it into the memory (position address) 
	 * The logic is
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <-  intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.store() now pc points to the parameter
	 * 6. pc -> extbus //(pc.read())the parameter address is the external bus
	 * 7. memory reads // memory reads the data in the parameter address. 
	 * 					// this data is the address where the RPG value must be stores 
	 * 8. memory stores //memory reads the address and wait for the value
	 * 9. RPG -> Externalbus //RPG.read()
	 * 10. memory stores //memory receives the value and stores it
	 * 11. pc -> intbus2 //pc.read() now pc must point the next instruction address
	 * 12. ula <- intbus2 //ula.store()
	 * 13. ula incs
	 * 14. ula -> intbus2 //ula.read()
	 * 15. pc <- intbus2 //pc.store() 
	 * end
	 * @param address
	 */
	public void store() {
		PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore(); //now PC points to the parameter address
		PC.read(); 
		memory.read();   //the parameter address (pointing to the addres where data must be stored
		                 //is now in externalbus1
		memory.store(); //the address is in the memory. Now we must to send the data
		RPG.read();
		memory.store(); //the data is now stored
		PC.internalRead(); //we need to make PC points to the next instruction address
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore(); //now PC points to the next instruction. We go back to the FETCH status.
	}
	
	/**
	 * This method implements the microprogram for
	 * 					ldi immediate
	 * In the machine language this command number is 7, and the immediate value
	 *        is in the position next to him
	 *    
	 * The method moves the value (parameter) into the internalbus1 and the RPG 
	 * (the first register in the register list) consumes it 
	 * The logic is
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <-  intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.store() now pc points to the parameter
	 * 6. pc -> extbus //(pc.read())the address where is the position to be read is now in the external bus 
	 * 7. memory reads from extbus //this forces memory to write the stored data in the extbus
	 * 8. RPG <- extbus //rpg.store()
	 * 9. 10. pc -> intbus2 //pc.read() now pc must point the next instruction address
	 * 10. ula <- intbus2 //ula.store()
	 * 11. ula incs
	 * 12. ula -> intbus2 //ula.read()
	 * 13. pc <- intbus2 //pc.store() 
	 * end
	 * @param address
	 */
	public void ldi() {
		PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore(); //now PC points to the parameter address
		PC.read(); 
		memory.read(); // the immediate is now in the external bus.
		RPG.store();   //RPG receives the immediate
		PC.internalRead(); //we need to make PC points to the next instruction address
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore(); //now PC points to the next instruction. We go back to the FETCH status.
	}
	
	/**
	 * This method implements the microprogram for
	 * 					inc 
	 * In the machine language this command number is 8
	 *    
	 * The method moves the value in rpg (the first register in the register list)
	 *  into the ula and performs an inc method
	 * 		-> inc works just like add rpg (the first register in the register list)
	 *         with the mumber 1 stored into the memory
	 * 		-> however, inc consumes lower amount of cycles  
	 * 
	 * The logic is
	 * 
	 * 1. rpg -> intbus1 //rpg.read()
	 * 2. ula  <- intbus1 //ula.store()
	 * 3. Flags <- zero //the status flags are reset
	 * 4. ula incs
	 * 5. ula -> intbus1 //ula.read()
	 * 6. ChangeFlags //informations about flags are set according the result
	 * 7. rpg <- intbus1 //rpg.store()
	 * 8. pc -> intbus2 //pc.read() now pc must point the next instruction address
	 * 9. ula <- intbus2 //ula.store()
	 * 10. ula incs
	 * 11. ula -> intbus2 //ula.read()
	 * 12. pc <- intbus2 //pc.store()
	 * end
	 * @param address
	 */
	public void inc() {
		RPG.internalRead();
		ula.store(1);
		ula.inc();
		ula.read(1);
		setStatusFlags(intbus1.get());
		RPG.internalStore();
		PC.internalRead(); //we need to make PC points to the next instruction address
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore(); //now PC points to the next instruction. We go back to the FETCH status.
	}
	
	/**
	 * This method implements the microprogram for
	 * 					move <reg1> <reg2> 
	 * In the machine language this command number is 9
	 *    
	 * The method reads the two register ids (<reg1> and <reg2>) from the memory, in positions just after the command, and
	 * copies the value from the <reg1> register to the <reg2> register
	 * 
	 * 1. pc -> intbus2 //pc.read()
	 * 2. ula <-  intbus2 //ula.store()
	 * 3. ula incs
	 * 4. ula -> intbus2 //ula.read()
	 * 5. pc <- intbus2 //pc.store() now pc points to the first parameter
	 * 6. pc -> extbus //(pc.read())the address where is the position to be read is now in the external bus 
	 * 7. memory reads from extbus //this forces memory to write the parameter (first regID) in the extbus
	 * 8. pc -> intbus2 //pc.read() //getting the second parameter
	 * 9. ula <-  intbus2 //ula.store()
	 * 10. ula incs
	 * 11. ula -> intbus2 //ula.read()
	 * 12. pc <- intbus2 //pc.store() now pc points to the second parameter
	 * 13. demux <- extbus //now the register to be operated is selected
	 * 14. registers -> intbus1 //this performs the internal reading of the selected register 
	 * 15. PC -> extbus (pc.read())the address where is the position to be read is now in the external bus 
	 * 16. memory reads from extbus //this forces memory to write the parameter (second regID) in the extbus
	 * 17. demux <- extbus //now the register to be operated is selected
	 * 18. registers <- intbus1 //thid rerforms the external reading of the register identified in the extbus
	 * 19. 10. pc -> intbus2 //pc.read() now pc must point the next instruction address
	 * 20. ula <- intbus2 //ula.store()
	 * 21. ula incs
	 * 22. ula -> intbus2 //ula.read()
	 * 23. pc <- intbus2 //pc.store()  
	 * 		  
	 */
	public void moveRegReg() {
		PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore(); //now PC points to the first parameter (the first reg id)
		PC.read(); 
		memory.read(); // the first register id is now in the external bus.
		PC.internalRead();
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore(); //now PC points to the second parameter (the second reg id)
		demux.setValue(extbus1.get()); //points to the correct register
		registersInternalRead(); //starts the read from the register identified into demux bus
		PC.read();
		memory.read(); // the second register id is now in the external bus.
		demux.setValue(extbus1.get());//points to the correct register
		registersInternalStore(); //performs an internal store for the register identified into demux bus
		PC.internalRead(); //we need to make PC points to the next instruction address
		ula.internalStore(1);
		ula.inc();
		ula.internalRead(1);
		PC.internalStore(); //now PC points to the next instruction. We go back to the FETCH status.
	}
	
	
	public ArrayList<Register> getRegistersList() {
		return registersList;
	}

	/**
	 * This method performs an (external) read from a register into the register list.
	 * The register id must be in the demux bus
	 */
	private void registersRead() {
		registersList.get(demux.getValue()).read();
	}
	
	/**
	 * This method performs an (internal) read from a register into the register list.
	 * The register id must be in the demux bus
	 */
	private void registersInternalRead() {
		registersList.get(demux.getValue()).internalRead();;
	}
	
	/**
	 * This method performs an (external) store toa register into the register list.
	 * The register id must be in the demux bus
	 */
	private void registersStore() {
		registersList.get(demux.getValue()).store();
	}
	
	/**
	 * This method performs an (internal) store toa register into the register list.
	 * The register id must be in the demux bus
	 */
	private void registersInternalStore() {
		registersList.get(demux.getValue()).internalStore();;
	}



	/**
	 * This method reads an entire file in machine code and
	 * stores it into the memory
	 * NOT TESTED
	 * @param filename
	 * @throws IOException 
	 */
	public void readExec(String filename) throws IOException {
		   BufferedReader br = new BufferedReader(new		 
		   FileReader(filename+".dxf"));
		   String linha;
		   int i=0;
		   while ((linha = br.readLine()) != null) {
			     extbus1.put(i);
			     memory.store();
			   	 extbus1.put(Integer.parseInt(linha));
			     memory.store();
			     i++;
			}
			br.close();
	}
	
	/**
	 * This method executes a program that is stored in the memory
	 */
	public void controlUnitEexec() {
		halt = false;
		while (!halt) {
			fetch();
			decodeExecute();
		}

	}
	

	/**
	 * This method implements The decode proccess,
	 * that is to find the correct operation do be executed
	 * according the command.
	 * And the execute proccess, that is the execution itself of the command
	 */
	private void decodeExecute() {
		IR.internalRead(); //the instruction is in the internalbus2
		int command = intbus2.get();
		simulationDecodeExecuteBefore(command);
		switch (command) {
		case 0:
			addRegReg();
			break;
		case 1:
			addMemReg();
			break;
		case 2:
			addRegMem();
			break;
		case 3:
			addImmReg();
			break;
		case 4:
			subRegReg();
			break;
		case 5:
			subMemReg();
			break;
		case 6:
            subRegMem();
            break;
        case 7:
            subImmReg();
            break;
        case 8:
			jmp();
			break;
		case 9:
			jn();
			break;
        case 10:
            jz();
            break;
        case 11:
            jeq();
            break;
		case 12:
			read();
			break;
		case 13:
			store();
			break;
		case 14:
			ldi();
			break;
		case 15:
			inc();
			break;
		case 16:
			moveRegReg();
			break;
		default:
			halt = true;
			break;
		}
		if (simulation)
			simulationDecodeExecuteAfter();
	}

	/**
	 * This method is used to show the components status in simulation conditions
	 * NOT TESTED
	 * @param command 
	 */
	private void simulationDecodeExecuteBefore(int command) {
		System.out.println("----------BEFORE Decode and Execute phases--------------");
		String instruction;
		int parameter = 0;
		for (Register r:registersList) {
			System.out.println(r.getRegisterName()+": "+r.getData());
		}
		if (command !=-1)
			instruction = commandsList.get(command);
		else
			instruction = "END";
		if (hasOperands(instruction)) {
			parameter = memory.getDataList()[PC.getData()+1];
			System.out.println("Instruction: "+instruction+" "+parameter);
		}
		else
			System.out.println("Instruction: "+instruction);
		if ("read".equals(instruction))
			System.out.println("memory["+parameter+"]="+memory.getDataList()[parameter]);
		
	}

	/**
	 * This method is used to show the components status in simulation conditions
	 * NOT TESTED 
	 */
	private void simulationDecodeExecuteAfter() {
		String instruction;
		System.out.println("-----------AFTER Decode and Execute phases--------------");
		System.out.println("Internal Bus 1: "+intbus1.get());
		System.out.println("Internal Bus 2: "+intbus2.get());
		System.out.println("External Bus 1: "+extbus1.get());
		for (Register r:registersList) {
			System.out.println(r.getRegisterName()+": "+r.getData());
		}
		Scanner entrada = new Scanner(System.in);
		System.out.println("Press <Enter>");
		String mensagem = entrada.nextLine();
	}

	/**
	 * This method uses PC to find, in the memory,
	 * the command code that must be executed.
	 * This command must be stored in IR
	 * NOT TESTED!
	 */
	private void fetch() {
		PC.read();
		memory.read();
		IR.store();
		simulationFetch();
	}

	/**
	 * This method is used to show the components status in simulation conditions
	 * NOT TESTED!!!!!!!!!
	 */
	private void simulationFetch() {
		if (simulation) {
			System.out.println("-------Fetch Phase------");
			System.out.println("PC: "+PC.getData());
			System.out.println("IR: "+IR.getData());
		}
	}

	/**
	 * This method is used to show in a correct way the operands (if there is any) of instruction,
	 * when in simulation mode
	 * NOT TESTED!!!!!
	 * @param instruction 
	 * @return
	 */
	private boolean hasOperands(String instruction) {
		if ("inc".equals(instruction)) //inc is the only one instruction having no operands
			return false;
		else
			return true;
	}

	/**
	 * This method returns the amount of positions allowed in the memory
	 * of this architecture
	 * NOT TESTED!!!!!!!
	 * @return
	 */
	public int getMemorySize() {
		return memorySize;
	}
	
	public static void main(String[] args) throws IOException {
		Architecture arch = new Architecture(true);
		arch.readExec("program");
		arch.controlUnitEexec();
	}
	

}
