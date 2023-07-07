# motion-correlator

This project is for the central "brain" of a multi-camera CCTV security system.

Each individual CCTV camera uses a Raspberry Pi running [PiKrellCam](https://github.com/ruth-connect/pikrellcam), where each camera performs its own individual motion detection.

This application polls the MJPEG stream of each camera, and stores the last few seconds of footage in memory.

When motion is detected, a shell script reads motion vectors from the PiKrellCam Motion FIFO and sends an HTTP request to this application, where we then perform OpenCV HOG (Histogram of Oriented Gradients) person detection on the last few seconds of footage.

The ultimate aim of this project is to correlate the co-ordinates of the motion vectors from the PiKrellCam Motion FIFO with the co-ordinates of person detections from OpenCV, to determine the overall probability of there being a person in the image.

This project also communicates with a Home Assistant instance running on the local network, so that we can notify Home Assistant when motion or a person has been detected by a given camera, and Home Assistant can notify us when a burglar alarm sensor has been triggered, so we can automatically initiate video recording (and person detection) on camera(s) in the vicinity of the alarm sensor.

We also send a "heartbeat" message to Home Assistant every few seconds, so that Home Assistant can raise an alert if the expected heartbeat message is not received.
