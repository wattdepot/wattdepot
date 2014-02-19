"""Python client library for the Wattdepot Platform.

This client library is designed to support the wattdepot data query and manipulation.

A typical usage of this API might look like this:

    wclient = wattdepot.WattdepotClient(server_uri, orgid, username, password)
    wclient.get_value("a_sensor_id")
    
"""

import datetime
import requests
from requests.exceptions import Timeout
import json
import jsonpickle


class MeasurementType:
    """Define the measurement type used in wattdepot."""
    def __init__(self, id, name, units):
        self.id = id
        self.name = name
        self.units = units

class Depository:
    """Define the Depository used in wattdept."""
    def __init__(self, id, name, measurementType, organizationId):
        self.id = id
        self.name = name
        self.measurementType = measurementType    
        self.organizationId = organizationId

class Sensor:
    """Define the Sensor used in wattdepot."""
    def __init__(self, id, name, uri, modelId, ownerId):
        self.id = id
        self.name = name
        self.uri = uri
        self.modelId = modelId
        self.ownerId = ownerId
            
class Measurement:
    """Define the Measurement used in Wattdepot."""
    def __init__(self, id, sensorId, date, value, measurementType):
        self.id = id
        self.sensorId = sensorId
        self.date = date
        self.value = value
        self.measurementType = measurementType
        
class WattdepotClient:
    """Define the wattdepot client."""

    def __init__(self, server_uri, orgid, username, password):
        """Constructor"""
        self.session = requests.session()
        self.session.timeout = 5
        self.session.auth = (username, password)
        
        self.server_url = "%s/wattdepot/%s" % (server_uri, orgid)
        self.server_uri = server_uri
        self.orgid = orgid
        self.ownerId = username
    
    def get_depositories(self):
        """returns a list of depositories of the server."""
        url = "%s/depositories/" % (self.server_url)
        response = self.session.get(url)
        return response.text
    
    def get_depository(self, depository_id):
        """returns a list of depositories of the server."""
        url = "%s/depository/%s" % (self.server_url, depository_id)
        response = self.session.get(url)
        obj = json.loads(response.text)
        mt_dict = obj["measurementType"]
        measurementType = MeasurementType(mt_dict["id"], mt_dict["name"], mt_dict["units"])
        return Depository(obj["id"], obj["name"], measurementType, obj["organizationId"])
    
    def get_measurement_type(self, measurement_type_id):
        """returns the measurement type."""
        url = "%s/wattdepot/public/measurement-type/%s" % (self.server_uri, measurement_type_id)
        response = self.session.get(url)
        obj = json.loads(response.text)
        return MeasurementType(obj["id"], obj["name"], obj["units"])
      
    def create_depository(self, depository_id, depository_name, measurement_type_id):
        """create a depository."""
        measurement_type = self.get_measurement_type(measurement_type_id)
        depository = Depository(depository_id, depository_name, measurement_type, self.orgid)
        url = "%s/depository/" % (self.server_url)
        self.session.put(url, jsonpickle.encode(depository))
        
    def get_sensors(self):
        """returns a list of sensors of the server."""
        url = "%s/sensors/" % (self.server_url)
        response = self.session.get(url)
        return response.text
    
    def get_sensor(self, sensor_id):
        """returns the sensor."""
        url = "%s/sensor/%s" % (self.server_uri, sensor_id)
        response = self.session.get(url)
        obj = json.loads(response.text)
        return Sensor(obj["id"], obj["name"], obj["uri"], obj["modelId"], obj["ownerId"])

    def create_sensor(self, id, name, uri, modelId):
        """create a sensor."""
        sensor = Sensor(id, name, uri, modelId, self.ownerId)
        url = "%s/sensor/" % (self.server_url)
        self.session.put(url, jsonpickle.encode(sensor))
    
    def get_latest_value(self, depository_id, sensor_id):
        """Returns the latest usage of the specified resource."""
        self.session.params = {'sensor': sensor_id,
                          'latest': "true"}
        url = "%s/depository/%s/value/" % (self.server_url,depository_id)
        return self._process_value(url, sensor_id)

    def get_value(self, depository_id, sensor_id, timestamp):
        """Return the resource usage for the timestamp."""
        self.session.params = {'sensor': sensor_id,
                          'timestamp': timestamp}
        url = "%s/depository/%s/value/" % (self.server_url,depository_id)
        return self._process_value(url, sensor_id)
    
    def get_measurements(self, depository_id, sensor_id, start, end):
        """returns the list of measurements."""
        self.session.params = {'sensor': sensor_id,
                          'start': start.isoformat(),
                          'end': end.isoformat()}
        url = "%s/depository/%s/measurements/" % (self.server_url,depository_id)
        response = self.session.get(url)
        return response.text
    
    def put_measurement(self, depository_id, sensor_id, timestamp, value, units):
        """create a measurement."""
        
        # java date format yyyy-MM-dd'T'HH:mm:ss.SSSZ
        # python isoformat YYYY-MM-DDTHH:MM:SS.mmmmmm
        timestamp = timestamp.isoformat()
        measurement = Measurement(sensor_id, sensor_id, timestamp, value, units)
        url = "%s/depository/%s/measurement/" % (self.server_url, depository_id)
        self.session.put(url, jsonpickle.encode(measurement))

    def _process_value(self, url, sensor_id):
        """send the request and process the value returned from the response."""
        try:
            response = self.session.get(url)
            usage = self._get_value_from_json(response.text)
            return abs(int(round(float(usage))))
        except Timeout:
            print 'Wattdepot data retrieval for team %s error: connection timeout.' % sensor_id

        return 0

    def _get_value_from_json(self, json_response):
        """get the usage from the JSON response"""
        value_object = json.loads(json_response)
        value = value_object["value"]
        if value:
            return value
        else:
            return 0
