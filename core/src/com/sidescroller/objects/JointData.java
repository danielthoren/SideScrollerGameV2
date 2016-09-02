package com.sidescroller.objects;

/**
 * Container that contains data that is needed for a joint to have. Each field might not have a proper value, if not
 * then theese values are set to null so that it can be checked when looking for a specific data.
 */
public class JointData {

	private String name;
	private Integer jointId;

	/**
	 * Creates a 'JointData' container.
	 */
	public JointData() {
		jointId = null;
		name = null;
	}

	/**
	 * The name of the joint. This parameter is set in the editor and thus might be null. Not individual.
	 * Used when creating custom objects that needs to have specific joints known.
	 * @return The name of the joint.
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * The id of the joint. This parameter is set in the editor and thus might be null. Not individual.
	 * Used when actions remove or modify joint.
	 * @return The id of the joint.
	 */
	public Integer getJointId() {
		return jointId;
	}

	public void setJointId(Integer jointId) {
		this.jointId = jointId;
	}
}
