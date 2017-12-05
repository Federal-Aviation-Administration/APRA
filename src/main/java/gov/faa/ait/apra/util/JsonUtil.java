/*
 * Federal Aviation Administration (FAA) public work 
 * 
 * As a work of the United States Government, this project is in the 
 * public domain within the United States. Additionally, we waive copyright 
 * and related rights in the work worldwide 
 * through the Creative Commons 0 (CC0) 1.0 Universal public domain dedication
 * 
 * APRA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 */
package gov.faa.ait.apra.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
/**
 * Unmarshalling utility class for json data.   Target type must extend this.
 * This construct was due to Java Generics and type erasure.
 * 
 * @author FAA
 *
 * @param <T>
 */
public abstract class JsonUtil<T> {

    /**
     * 
     * @param unbound
     * @return
     * @throws IOException
     * @throws JsonParseException
     * @throws JsonMappingException
     */
	@SuppressWarnings("unchecked")
	public T unmarshalJson(String unbound)
			throws IOException, JsonParseException, JsonMappingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
		Class<?> targetClass = (Class<T>) GenericsUtil.getTypeArguments(JsonUtil.class, this.getClass()).get(0);
		return (T) mapper.readValue(unbound.getBytes(Charsets.UTF_16), targetClass);
	}
}
