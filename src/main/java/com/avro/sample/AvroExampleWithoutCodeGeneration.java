package com.avro.sample;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;

public class AvroExampleWithoutCodeGeneration {

	public static void main(String[] args) throws IOException {

		AvroExampleWithoutCodeGeneration codeGenerator = new AvroExampleWithoutCodeGeneration();

		{

			InputStream in = new FileInputStream("/home/raghunandangupta/excl/avro-demo/src/main/resources/StudentActivity.asvc");

			// create a schema
			Schema schema = new Schema.Parser().parse(new File("/home/raghunandangupta/excl/avro-demo/src/main/resources/StudentActivity.asvc"));
			
			// create a record to hold json
			GenericRecord avroRecords = new GenericData.Record(schema);
			
			// create a record to hold course_details
			GenericRecord CourseRec = new GenericData.Record(schema.getField("course_details").schema());
			
			// this file will have AVro output data
			File avroFile = new File("StudentActivity.avro");
			avroFile.createNewFile();
			
			// Create a writer to serialize the record
			DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<GenericRecord>(schema);		         
			DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<GenericRecord>(datumWriter);

			dataFileWriter.create(schema, avroFile);
			
			// iterate over JSONs present in input file and write to Avro output file
			for (Iterator it = new ObjectMapper().readValues(
					new JsonFactory().createJsonParser(in), JSONObject.class); it.hasNext();) {

				JSONObject JsonRec = (JSONObject) it.next();
				avroRecords.put("id", JsonRec.get("id"));
				avroRecords.put("student_id", JsonRec.get("student_id"));
				avroRecords.put("university_id", JsonRec.get("university_id"));

				LinkedHashMap CourseDetails = (LinkedHashMap) JsonRec.get("course_details");
				CourseRec.put("course_id", CourseDetails.get("course_id"));
				CourseRec.put("enroll_date", CourseDetails.get("enroll_date"));
				CourseRec.put("verb", CourseDetails.get("verb"));
				CourseRec.put("result_score", CourseDetails.get("result_score"));

				avroRecords.put("course_details", CourseRec);

				dataFileWriter.append(avroRecords);
			}  // end of for loop

			in.close();
			dataFileWriter.close();
			
			

		}

	}

}
