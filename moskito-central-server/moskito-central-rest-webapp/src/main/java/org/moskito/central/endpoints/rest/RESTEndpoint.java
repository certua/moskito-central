package org.moskito.central.endpoints.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.moskito.central.Central;
import org.moskito.central.Snapshot;
import org.moskito.central.SnapshotMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Central REST resource for incoming snapshots via HTTP.
 * 
 * @author dagafonov
 * 
 */
@Path("/central")
public class RESTEndpoint {

	private static final Logger logger = LoggerFactory.getLogger("RESTEndpoint");

	/**
	 * Central instance.
	 */
	private Central central;

	/**
	 * Default constructor.
	 */
	public RESTEndpoint() {
		central = Central.getInstance();
	}

    /**
     * Sends {@link Snapshot} to the endpoint client.
     *
     * @return requested {@link Snapshot}
     */
	@GET
	@Path("/getSnapshot")
	@Produces({ MediaType.APPLICATION_JSON })
	public Snapshot getSnapshot() {
		Snapshot snapshot = new Snapshot();

		SnapshotMetaData metaData = new SnapshotMetaData();
		metaData.setProducerId("prodId");
		metaData.setCategory("catId");
		metaData.setSubsystem("subSId");

		snapshot.setMetaData(metaData);

		HashMap<String, String> data = new HashMap<>();
		data.put("firstname", "moskito");
		data.put("lastname", "central");
		snapshot.addSnapshotData("test", data);
		snapshot.addSnapshotData("test2", data);
		snapshot.addSnapshotData("test3", data);

		return snapshot;
	}

	/**
	 * Receives {@link Snapshot} in order to transfer it to the central.
	 * 
	 * @param snapshot received {@link Snapshot}
	 */
	@POST
	@Path("/addSnapshot")
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response addSnapshot(Snapshot snapshot) {
		try{
			central.processIncomingSnapshot(snapshot);
			return Response.ok("SUCCESS", MediaType.APPLICATION_JSON).build();
		}catch (Exception e){
			logger.error(e.getMessage(), e.getCause());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("FAILED: " + e.getMessage()).build();
		}
	}
}
