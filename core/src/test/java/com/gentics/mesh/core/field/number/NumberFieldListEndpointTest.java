package com.gentics.mesh.core.field.number;

import static com.gentics.mesh.test.TestDataProvider.PROJECT_NAME;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.gentics.ferma.Tx;
import com.gentics.mesh.core.data.NodeGraphFieldContainer;
import com.gentics.mesh.core.data.node.Node;
import com.gentics.mesh.core.data.node.field.list.impl.NumberGraphFieldListImpl;
import com.gentics.mesh.core.field.AbstractListFieldEndpointTest;
import com.gentics.mesh.core.rest.node.NodeResponse;
import com.gentics.mesh.core.rest.node.field.Field;
import com.gentics.mesh.core.rest.node.field.list.impl.NumberFieldListImpl;
import com.gentics.mesh.test.TestSize;
import com.gentics.mesh.test.context.MeshTestSetting;

@MeshTestSetting(useElasticsearch = false, testSize = TestSize.PROJECT_AND_NODE, startServer = true)
public class NumberFieldListEndpointTest extends AbstractListFieldEndpointTest {

	@Override
	public String getListFieldType() {
		return "number";
	}

	@Test
	@Override
	public void testCreateNodeWithField() {
		try (Tx tx = tx()) {
			NumberFieldListImpl listField = new NumberFieldListImpl();
			listField.add(42L);
			listField.add(41L);
			listField.add(0.1f);
			listField.add(Long.MAX_VALUE);

			NodeResponse response = createNode(FIELD_NAME, listField);
			NumberFieldListImpl field = response.getFields().getNumberFieldList(FIELD_NAME);
			assertThat(field.getItems()).as("Only valid values should be stored").containsExactly(42, 41, 0.1,
					Long.MAX_VALUE);
		}
	}

	@Test
	@Override
	public void testNullValueInListOnCreate() {
		try (Tx tx = tx()) {
			NumberFieldListImpl listField = new NumberFieldListImpl();
			listField.add(42);
			listField.add(41);
			listField.add(null);
			createNodeAndExpectFailure(FIELD_NAME, listField, BAD_REQUEST, "field_list_error_null_not_allowed",
					FIELD_NAME);
		}
	}

	@Test
	@Override
	public void testNullValueInListOnUpdate() {
		try (Tx tx = tx()) {
			NumberFieldListImpl listField = new NumberFieldListImpl();
			listField.add(42);
			listField.add(41);
			listField.add(null);
			updateNodeFailure(FIELD_NAME, listField, BAD_REQUEST, "field_list_error_null_not_allowed", FIELD_NAME);
		}
	}

	@Test
	@Override
	public void testCreateNodeWithNoField() {
		try (Tx tx = tx()) {
			NodeResponse response = createNode(FIELD_NAME, (Field) null);
			assertThat(response.getFields().getNumberFieldList(FIELD_NAME)).as("List field in reponse should be null")
					.isNull();
		}
	}

	@Test
	@Override
	public void testUpdateSameValue() {
		try (Tx tx = tx()) {
			NumberFieldListImpl listField = new NumberFieldListImpl();
			listField.add(41L);
			listField.add(42L);

			NodeResponse firstResponse = updateNode(FIELD_NAME, listField);
			String oldVersion = firstResponse.getVersion().getNumber();

			NodeResponse secondResponse = updateNode(FIELD_NAME, listField);
			assertThat(secondResponse.getVersion().getNumber()).as("New version number").isEqualTo(oldVersion);
		}
	}

	@Test
	@Override
	public void testReadNodeWithExistingField() {
		try (Tx tx = tx()) {
			// 1. Update an existing node
			NumberFieldListImpl listField = new NumberFieldListImpl();
			listField.add(41L);
			listField.add(42L);
			NodeResponse firstResponse = updateNode(FIELD_NAME, listField);

			// 2. Read the node
			NodeResponse response = readNode(PROJECT_NAME, firstResponse.getUuid());
			NumberFieldListImpl deserializedField = response.getFields().getNumberFieldList(FIELD_NAME);
			assertNotNull(deserializedField);
			assertThat(deserializedField.getItems()).as("List field values from updated node (null values are omitted)")
					.containsExactly(41, 42);
		}
	}

	@Test
	@Override
	public void testUpdateNodeFieldWithField() throws IOException {
		try (Tx tx = tx()) {
			Node node = folder("2015");

			List<List<Number>> valueCombinations = Arrays.asList(Arrays.asList(1.1, 2, 3), Arrays.asList(3, 2, 1.1),
					Collections.emptyList(), Arrays.asList(47.11, 8.15), Arrays.asList(3));

			NodeGraphFieldContainer container = node.getGraphFieldContainer("en");
			for (int i = 0; i < 20; i++) {
				List<Number> oldValue = getListValues(container, NumberGraphFieldListImpl.class, FIELD_NAME);
				List<Number> newValue = valueCombinations.get(i % valueCombinations.size());

				NumberFieldListImpl list = new NumberFieldListImpl();
				for (Number value : newValue) {
					list.add(value);
				}
				NodeResponse response = updateNode(FIELD_NAME, list);
				NumberFieldListImpl field = response.getFields().getNumberFieldList(FIELD_NAME);
				assertThat(field.getItems()).as("Updated field").containsExactlyElementsOf(list.getItems());
				node.reload();
				container.reload();

				NodeGraphFieldContainer newContainerVersion = container.getNextVersion();
				assertEquals("Check version number", newContainerVersion.getVersion().toString(),
						response.getVersion().getNumber());
				assertEquals("Check old value", oldValue,
						getListValues(container, NumberGraphFieldListImpl.class, FIELD_NAME));
				container = newContainerVersion;
			}
		}
	}

	@Test
	@Override
	public void testUpdateSetNull() {
		Node node = folder("2015");

		try (Tx tx = tx()) {
			NumberFieldListImpl list = new NumberFieldListImpl();
			list.add(42);
			list.add(41.1f);
			NodeResponse firstResponse = updateNode(FIELD_NAME, list);
			String oldVersion = firstResponse.getVersion().getNumber();

			NodeResponse secondResponse = updateNode(FIELD_NAME, null);
			assertThat(secondResponse.getFields().getNumberFieldList(FIELD_NAME)).as("Updated Field").isNull();
			assertThat(oldVersion).as("Version should be updated")
					.isNotEqualTo(secondResponse.getVersion().getNumber());

			// Assert that the old version was not modified
			node.reload();
			NodeGraphFieldContainer latest = node.getLatestDraftFieldContainer(english());
			assertThat(latest.getVersion().toString()).isEqualTo(secondResponse.getVersion().getNumber());
			assertThat(latest.getNumberList(FIELD_NAME)).isNull();
			assertThat(latest.getPreviousVersion().getNumberList(FIELD_NAME)).isNotNull();
			List<Number> oldValueList = latest.getPreviousVersion().getNumberList(FIELD_NAME).getList().stream()
					.map(item -> item.getNumber()).collect(Collectors.toList());
			assertThat(oldValueList).containsExactly(42L, 41.1);

			NodeResponse thirdResponse = updateNode(FIELD_NAME, null);
			assertEquals("The field does not change and thus the version should not be bumped.",
					thirdResponse.getVersion().getNumber(), secondResponse.getVersion().getNumber());
		}
	}

	@Test
	@Override
	public void testUpdateSetEmpty() {
		try (Tx tx = tx()) {
			NumberFieldListImpl list = new NumberFieldListImpl();
			list.add(42);
			list.add(41.1f);
			NodeResponse firstResponse = updateNode(FIELD_NAME, list);
			String oldVersion = firstResponse.getVersion().getNumber();

			NumberFieldListImpl emptyField = new NumberFieldListImpl();
			NodeResponse secondResponse = updateNode(FIELD_NAME, emptyField);
			assertThat(secondResponse.getFields().getNumberFieldList(FIELD_NAME)).as("Updated field list").isNotNull();
			assertThat(secondResponse.getFields().getNumberFieldList(FIELD_NAME).getItems())
					.as("Field value should be truncated").isEmpty();
			assertThat(secondResponse.getVersion().getNumber()).as("New version number should be generated")
					.isNotEqualTo(oldVersion);

			NodeResponse thirdResponse = updateNode(FIELD_NAME, emptyField);
			assertEquals("The field does not change and thus the version should not be bumped.",
					thirdResponse.getVersion().getNumber(), secondResponse.getVersion().getNumber());
			assertThat(secondResponse.getVersion().getNumber()).as("No new version number should be generated")
					.isEqualTo(secondResponse.getVersion().getNumber());
		}
	}

}
