package com.outbound;

/**
 * @author Created by M_ElHagez on Apr 12, 2021 
 */
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Test {
	public static void runDisplay() {
		String lolo = "heloo";
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		try {
			engine.eval(new FileReader("app2.js"));
			Invocable invocable = (Invocable) engine;
			Object result;
			result = invocable.invokeFunction("display", lolo);
			System.out.println(result);
			System.out.println(result.getClass());
		} catch (FileNotFoundException | NoSuchMethodException | ScriptException e) {
			e.printStackTrace();
		}
	}
	public static void uploadCsvFile() {
		String lolo = "heloo";
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		try {
			engine.eval(new FileReader("app2.js"));
			Invocable invocable = (Invocable) engine;
			Object result;
			result = invocable.invokeFunction("display", lolo);
			System.out.println(result);
			System.out.println(result.getClass());
		} catch (FileNotFoundException | NoSuchMethodException | ScriptException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		runDisplay();

	}

}
