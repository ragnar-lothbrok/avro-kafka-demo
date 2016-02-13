package com.avro.sample;

import java.io.File;
import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonProcessingException;

public class AvroExampleWithCodeGeneration {

	public void serialize() throws JsonParseException, JsonProcessingException, IOException {

		File store = File.createTempFile("studentactivity", ".avro");
		
		Activity a = new Activity();
		StudentActivity sa = new StudentActivity();
		sa.setId("IDUNIQUE");
		sa.setStudentId(1234);
		sa.setUniversityId(3456);

		a.setCourseId(121212);
		a.setEnrollDate("1212-12-12");
		a.setVerb("Verb");
		a.setResultScore(3434.343);

		sa.setCourseDetails(a);

		DatumWriter<StudentActivity> datumWriter = new GenericDatumWriter<StudentActivity>();
		DataFileWriter<StudentActivity> dataFileWriter = new DataFileWriter<StudentActivity>(datumWriter);

		dataFileWriter.create(a.getSchema(), store);

		dataFileWriter.append(sa);

		dataFileWriter.close();

	} // end of serialize method

	public void deserialize() throws IOException {
		// create a schema
		Schema schema = new Schema.Parser()
				.parse(new File("/home/raghunandangupta/excl/avro-demo/src/main/resources/StudentActivity.asvc"));
		// create a record using schema
		GenericRecord AvroRec = new GenericData.Record(schema);
		File AvroFile = new File("resources/StudentActivity1.avro");
		DatumReader<GenericRecord> datumReader = new GenericDatumReader<GenericRecord>(schema);
		DataFileReader<GenericRecord> dataFileReader = new DataFileReader<GenericRecord>(AvroFile, datumReader);
		System.out.println("Deserialized data is :");
		while (dataFileReader.hasNext()) {
			AvroRec = dataFileReader.next(AvroRec);
			System.out.println(AvroRec);
		}
	}

	public static void main(String[] args) throws JsonParseException, JsonProcessingException, IOException {
		AvroExampleWithCodeGeneration AvroEx = new AvroExampleWithCodeGeneration();
		AvroEx.serialize();
		// AvroEx.deserialize();
	}

}
