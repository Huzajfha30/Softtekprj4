module CommonAGV {
    // Allows use of Jackson @JsonProperty, @JsonInclude
    requires com.fasterxml.jackson.annotation;

    opens dk.sdu.sm4.common.agv to com.fasterxml.jackson.databind;

    // Export the API for other modules
    exports dk.sdu.sm4.common.agv;
}
