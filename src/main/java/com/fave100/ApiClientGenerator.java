package com.fave100;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ApiClientGenerator
{
	private static String ENTITY_PACKAGE = "com.fave100.client.generated.entities";
	private static String SERVICE_PACKAGE = "com.fave100.client.generated.services";

	private static String folderPath = "src/main/java/com/fave100/client/generated/";
	private static String servicePath = "";
	private static String entitiesPath = "";
	private static List<String> services = new ArrayList<>();

	public static void main(String[] args) throws IOException
	{
		// Delete old generated files
		deleteFiles(new File(folderPath));

		// TODO: Remove all the harcoding paths
		String apiDocDir = "src/main/webapp/apidocs";

		servicePath = folderPath + "/services/";
		entitiesPath = folderPath + "/entities/";

		new File(servicePath).mkdirs();
		new File(entitiesPath).mkdirs();

		for (File file : new File(apiDocDir).listFiles()) {
			if (file.isFile() && file.getName().endsWith(".json")) {
				BufferedReader in = new BufferedReader(new FileReader(file));

				String inputLine = "";
				String temp = null;
				while ((temp = in.readLine()) != null)
					inputLine += temp;
				in.close();

				Object obj = JSONValue.parse(inputLine);
				JSONObject json = (JSONObject)obj;

				generateService(json);
				generateEntities(json);
				generateServiceFactory();
			}
		}
	}

	private static void deleteFiles(File dir) {
		for (File file : dir.listFiles()) {
			if (file.isDirectory())
				deleteFiles(file);
			file.delete();
		}
	}

	public static void generateService(JSONObject json) throws FileNotFoundException, UnsupportedEncodingException {
		FileBuilder fb = new FileBuilder();

		JSONArray apis = (JSONArray)json.get("apis");

		String resourcePath = (String)json.get("resourcePath");
		// Not a service
		if (resourcePath == null)
			return;

		String resourceName = resourcePath.replace("/", "");
		String serviceName = ucFirst(resourceName) + "Service";
		services.add(serviceName);

		fb.append(getWarningComment());

		fb.append(String.format("package %s;", SERVICE_PACKAGE));

		fb.append("\n\n");

		Set<String> imports = new HashSet<>();
		imports.add("javax.ws.rs.Path");

		// Add Jackson annotations
		for (Object apiObj : apis) {
			JSONObject api = (JSONObject)apiObj;

			for (Object operationObj : (JSONArray)api.get("operations")) {
				JSONObject operation = (JSONObject)operationObj;
				String httpMethod = (String)operation.get("method");

				imports.add("javax.ws.rs." + httpMethod);
			}
		}

		// General imports
		imports.add("javax.ws.rs.PathParam");
		imports.add("javax.ws.rs.QueryParam");
		imports.add("com.gwtplatform.dispatch.rest.shared.RestAction");
		imports.add("com.gwtplatform.dispatch.rest.shared.RestService");

		// Import all needed entities;
		for (Object apiObj : apis) {
			JSONObject api = (JSONObject)apiObj;

			for (Object operationObj : (JSONArray)api.get("operations")) {
				JSONObject operation = (JSONObject)operationObj;
				String responseType = getClassName((String)operation.get("type"));

				if (!isBasicType(responseType))
					imports.add(ENTITY_PACKAGE + "." + responseType);

				for (Object paramObj : (JSONArray)operation.get("parameters")) {
					JSONObject param = (JSONObject)paramObj;
					String paramType = getClassName((String)param.get("type"));

					if (!isBasicType(paramType))
						imports.add(ENTITY_PACKAGE + "." + paramType);
				}
			}
		}

		for (String importString : imports) {
			fb.append("import ");
			fb.append(importString);
			fb.append(";\n");
		}

		// Path anno
		fb.append("\n@Path(\"/\")");

		// Interface
		fb.append(String.format("\npublic interface %s extends RestService {\n\n", serviceName));

		for (Object apiObj : apis) {
			JSONObject api = (JSONObject)apiObj;

			for (Object operationObj : (JSONArray)api.get("operations")) {

				JSONObject operation = (JSONObject)operationObj;
				String response = (String)operation.get("type");

				String responseType = getClassName(response);

				fb.indent();
				fb.append(String.format("@%s\n", operation.get("method")));
				fb.applyIndent();
				fb.append(String.format("@Path(\"%s\")\n", api.get("path")));
				fb.applyIndent();
				fb.append(String.format("public RestAction<%s> %s (", responseType, operation.get("nickname")));

				// Add Query or Path params
				JSONArray params = (JSONArray)operation.get("parameters");

				int i = 1;
				int length = params.size();
				for (Object paramObj : params) {
					JSONObject param = (JSONObject)paramObj;

					// Add @QueryParam or @PathParam anno if needed 
					String paramType = (String)param.get("paramType");
					String paramName = (String)param.get("name");
					String type = (String)param.get("type");

					if (!type.equals("AppUser")) {
						if (paramType.equals("query")) {
							fb.append(String.format("@QueryParam(\"%s\") ", paramName));
						}
						else if (paramType.equals("path")) {
							fb.append(String.format("@PathParam(\"%s\") ", paramName));
						}

						if (paramType.equals("body")) {
							fb.append(ucFirst(type));
						}
						else {
							fb.append(getClassName(type));
						}

						fb.append(" ");
						fb.append(paramName);

						if (length > 1 && i != length) {
							fb.append(", ");
						}
					}

					i++;
				}

				fb.append(");\n\n");
				fb.outdent();

			}
		}

		fb.append("}");

		fb.save(servicePath + serviceName + ".java");
	}

	private static void generateEntities(JSONObject json) throws FileNotFoundException, UnsupportedEncodingException {

		JSONObject models = (JSONObject)json.get("models");
		// No entities
		if (models == null)
			return;

		for (Object modelObj : models.keySet()) {
			FileBuilder fb = new FileBuilder();

			JSONObject model = (JSONObject)models.get((String)modelObj);

			String className = getClassName((String)model.get("id"));

			fb.append(getWarningComment());

			fb.append(String.format("package %s;", ENTITY_PACKAGE));
			fb.append("\n\nimport java.util.List;");
			fb.append(String.format("\n\npublic class %s {\n\n", className));

			JSONObject properties = (JSONObject)model.get("properties");

			// Print property fields
			for (Object propObj : properties.keySet()) {
				String propName = (String)propObj;
				JSONObject propJson = (JSONObject)properties.get(propName);
				fb.indent();
				fb.append("private ");
				if (propJson.get("type") != null) {
					fb.append(convertPropertyToType(propJson));
				}
				else {
					fb.append(getClassName((String)(propJson.get("$ref"))));
				}
				fb.append(String.format(" %s;\n", propName));
				fb.outdent();
			}

			fb.append("\n");

			// Print getters and setters
			for (Object propObj : properties.keySet()) {
				String propName = (String)propObj;
				JSONObject propJson = (JSONObject)properties.get(propName);

				// Getter
				String returnType = "";
				if (propJson.get("type") != null) {
					returnType = convertPropertyToType(propJson);
				}
				else {
					returnType = getClassName((String)(propJson.get("$ref")));
				}
				fb.indent();
				fb.append(String.format("public %s", returnType));
				fb.append(returnType.equals("boolean") && !propName.equals("value") ? " is" : " get");
				fb.append(String.format("%s() {\n", ucFirst(propName)));
				fb.indent();
				fb.append(String.format("return this.%s;\n", propName));
				fb.outdent();
				fb.applyIndent();
				fb.append("}\n\n");
				fb.outdent();

				// Setter
				fb.indent();
				fb.append(String.format("public void set%s(", ucFirst(propName)));
				if (propJson.get("type") != null) {
					fb.append(convertPropertyToType(propJson));
				}
				else {
					fb.append(getClassName((String)(propJson.get("$ref"))));
				}
				fb.append(String.format(" %s){\n", propName));
				fb.indent();
				fb.append(String.format("this.%s = %s;\n", propName, propName));
				fb.outdent();
				fb.applyIndent();
				fb.append("}\n\n");
				fb.outdent();
			}

			fb.append("}");

			String fileName = entitiesPath + className + ".java";
			if (!new File(fileName).exists())
				fb.save(fileName);
		}
	}

	private static void generateServiceFactory() throws FileNotFoundException, UnsupportedEncodingException {
		FileBuilder fb = new FileBuilder();

		fb.append("package com.fave100.client.generated.services;\n\n");
		fb.append("import com.google.inject.Inject;\n\n");
		fb.append("public class RestServiceFactory {\n\n");

		// Variable declarations
		fb.indent();
		for (String service : services) {
			fb.append(String.format("private %s _%s;\n", service, lcFirst(service)));
			fb.applyIndent();
		}

		fb.append("\n");
		fb.applyIndent();
		fb.append("@Inject\n");
		fb.applyIndent();
		fb.append("public RestServiceFactory(");

		// Constructor params
		int i = 1;
		for (String service : services) {
			fb.append(service);
			fb.append(" ");
			fb.append(lcFirst(service));

			if (services.size() > 1 && i != services.size())
				fb.append(", ");

			i++;
		}

		fb.append(") {\n");

		// Field initialization    	
		fb.indent();
		for (String service : services) {
			fb.append(String.format(" _%s = %s;\n", lcFirst(service), lcFirst(service)));
			fb.applyIndent();
		}
		fb.outdent();
		fb.append("}\n\n");

		// Getters
		for (String service : services) {
			fb.applyIndent();
			fb.append(String.format("public %s %s() {\n", service, lcFirst(service.replace("Service", ""))));
			fb.indent();
			fb.append(String.format("return _%s;\n", lcFirst(service)));
			fb.outdent();
			fb.applyIndent();
			fb.append("}\n\n");
		}

		fb.outdent();
		fb.append("}");

		fb.save(servicePath + "RestServiceFactory.java");
	}

	private static String getWarningComment() {
		return "/*\n"
				+ "* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n"
				+ "*\n"
				+ "* WARNING: THIS IS A GENERATED FILE. ANY CHANGES YOU\n"
				+ "* MAKE WILL BE LOST THE NEXT TIME THIS FILE IS GENERATED\n"
				+ "*\n"
				+ "* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n"
				+ "*/\n\n";
	}

	private static String getClassName(String type) {
		return convertType(type);
	}

	private static String convertPropertyToType(JSONObject property) {
		String type = convertType((String)property.get("type"));

		JSONObject itemObj = (JSONObject)property.get("items");
		if (itemObj != null) {
			String itemType = (String)itemObj.get("$ref");

			if (itemType == null) {
				// Raw type, don't try to turn it into DTO
				itemType = (String)itemObj.get("type");
				type += "<" + convertType(itemType) + ">";
			}
			else {
				// DTO ref
				type += "<" + getClassName(convertType(itemType)) + ">";
			}
		}

		return type;
	}

	private static String convertType(String type) {
		switch (type) {
			case "string":
				return "String";

			case "array":
				return "List";

			case "integer":
				return "int";

			case "void":
				return "Void";

			default:
				return type;
		}
	}

	private static boolean isBasicType(String type) {
		switch (type) {
			case "String":
			case "List":
			case "int":
			case "Void":
				return true;

			default:
				return false;
		}
	}

	private static String ucFirst(String string) {
		return Character.toString(string.charAt(0)).toUpperCase() + string.substring(1);
	}

	private static String lcFirst(String string) {
		return Character.toString(string.charAt(0)).toLowerCase() + string.substring(1);
	}
}
