package sk.upjs.nosql_data_source.business;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import de.undercouch.bson4jackson.BsonFactory;
import sk.upjs.nosql_data_source.entity.SimpleStudent;
import sk.upjs.nosql_data_source.entity.Student;
import sk.upjs.nosql_data_source.entity.StudijnyProgram;
import sk.upjs.nosql_data_source.entity.Studium;
import sk.upjs.nosql_data_source.persist.DaoFactory;
import sk.upjs.nosql_data_source.persist.StudentDao;

public class StudentConvertor {

	private List<Student> getStudents() {
		StudentDao dao = DaoFactory.INSTANCE.getStudentDao();
		return dao.getAll();
	}
	
	private String getJSONFromObject(Object o, boolean format) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		if (format)
			mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		return mapper.writeValueAsString(o);
	}
	
	public List<String> getStudentsJSON(boolean format) throws JsonProcessingException {
		List<String> result = new ArrayList<String>();
		for (Student s: getStudents()) {
			result.add(getJSONFromObject(s, format));
		}
		return result;
	}
	
	private byte[] getBSONFromObject(Object o) throws JsonGenerationException, JsonMappingException, IOException {
		BsonFactory bsonFactory = new BsonFactory();
		ObjectMapper mapper = new ObjectMapper(bsonFactory);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		mapper.writeValue(outputStream, o);
		return outputStream.toByteArray();
	}
	
	public List<byte[]> getStudentsBSON() throws JsonGenerationException, JsonMappingException, IOException {
		List<byte[]> result = new ArrayList<>();
		for (Student s: getStudents()) {
			result.add(getBSONFromObject(s));
		}
		return result;
	}
	
	private String getXMLFromObject(Object o, boolean format) throws JsonProcessingException {
		XmlMapper xmlMapper = new XmlMapper();
		if (format) {
			xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		}
		return xmlMapper.writeValueAsString(o);
	}
	
	public List<String> getStudentsXML(boolean format) throws JsonProcessingException {
		List<String> result = new ArrayList<String>();
		for (Student s: getStudents()) {
			result.add(getXMLFromObject(s, format));
		}
		return result;
	}

	private String getYAMLFromObject(Object o, boolean format) throws JsonProcessingException {
		YAMLMapper mapper = new YAMLMapper();
		if (format)
			mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		return mapper.writeValueAsString(o);
	}
	
	public List<String> getStudentsYAML(boolean format) throws JsonProcessingException {
		List<String> result = new ArrayList<String>();
		for (Student s: getStudents()) {
			result.add(getYAMLFromObject(s, format));
		}
		return result;
	}

	private String getCSVFromObject(Object o, Class objectClass, boolean withHeader) throws JsonProcessingException {
		CsvMapper mapper = new CsvMapper();
		CsvSchema schema = mapper.schemaFor(objectClass);
		if (withHeader) {
			schema = schema.withHeader();
		}
		ObjectWriter objectWriter = mapper.writer(schema);
		return objectWriter.writeValueAsString(o);
	}
	
	public String getSimpleStudentsCSV(boolean withHeader) throws JsonProcessingException {
		StudentDao dao = DaoFactory.INSTANCE.getStudentDao();
		List<SimpleStudent> simpleStudents = dao.getSimpleStudents();
		return getCSVFromObject(simpleStudents, simpleStudents.getClass(), withHeader);
	}
	
	public static void main(String[] args) throws Throwable {
		StudentConvertor sc = new StudentConvertor();
//		List<String> students = sc.getStudentsJSON(false); 
//		for (String s: students) {
//			System.out.println(s);
//		}

//		List<byte[]> studentsBytes = sc.getStudentsBSON();
//		System.out.println(new String(studentsBytes.get(0)));

//		List<String> students = sc.getStudentsXML(true); 
//		for (String s: students) {
//			System.out.println(s);
//		}

//		List<String> students = sc.getStudentsYAML(false); 
//		for (String s: students) {
//			System.out.println(s);
//		}
		
//		String csvStudents = sc.getSimpleStudentsCSV(true);
//		System.out.println(csvStudents);
		
		List<StudijnyProgram> programy = new ArrayList<StudijnyProgram>();
		List<Student> students = sc.getStudents();
		for (Student s : students) {
			for (Studium st : s.getStudium()) {
				programy.add(st.getStudijnyProgram());
			}
		}
		String csvStudijneProgramy = sc.getCSVFromObject(programy, StudijnyProgram.class, true);
		System.out.println(csvStudijneProgramy);
	}
}
