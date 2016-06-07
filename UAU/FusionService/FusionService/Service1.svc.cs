using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Text;
using System.Windows.Forms;

namespace FusionService
{

    public class Service1 : IService1
    {

        public string executeCommand(CommandData cmdData)
        {
            WebOperationContext.Current.OutgoingResponse.Headers.Add("Access-Control-Allow-Origin", "*");
            WebOperationContext.Current.OutgoingResponse.Headers.Add("Access-Control-Allow-Methods", "POST");
            WebOperationContext.Current.OutgoingResponse.Headers.Add("Access-Control-Allow-Headers", "Content-Type, Accept");
            try
            {
                string cmd = cmdData.Command.ToLower();
                if (cmdData.Target.ToLower().Equals("ssi"))
                {

                    bool  failed = false;
                    bool pipeIsStarted = UDPListener.Instance.isRunning == StatusCode.UP ? true : false;

                    /* DEBUG START */
                    if (cmd.Equals("start"))
                        failed = !startStopPipe(0);
                    else if (cmd.Equals("stop"))
                         failed = !startStopPipe(1);
                    return "DEBUG_MSG:" + cmd + "-signal send";
                    /* DEBUG END */


                    if (cmd.Equals("start") && !pipeIsStarted)
                        failed = !startStopPipe(0);
                    else if (cmd.Equals("stop") && pipeIsStarted)
                        failed = !startStopPipe(1);
                    else
                        return "pipe is allready at this state";

                    if (!failed)
                        return cmd + "-signal send";
                    else
                        return "failed to send " + cmd + "-signal";
                    
                }
                else if (cmdData.Target.ToLower().Equals("vsm"))
                {
                    return VSMHandler.executeCommand(cmd, "res/prj/vsm");
                }
                else
                    return "Unknown target: " + cmdData.Target + ". Valid values are: ssi, vsm";
            }
            catch (Exception ex)
            {
                return ex.Message;
            }
        }

        public Tuple<SSIEvent[], string> getEmotion()
        {
            WebOperationContext.Current.OutgoingResponse.Headers.Add("Access-Control-Allow-Origin", "*");
            WebOperationContext.Current.OutgoingResponse.Headers.Add("Access-Control-Allow-Methods", "GET");
            WebOperationContext.Current.OutgoingResponse.Headers.Add("Access-Control-Allow-Headers", "Content-Type, Accept");
            return new Tuple<SSIEvent[], string>(UDPListener.Instance.CurrentValues, UDPListener.Instance.CurrentError);
        }

        public Status getStatus()
        {
            WebOperationContext.Current.OutgoingResponse.Headers.Add("Access-Control-Allow-Origin", "*");
            WebOperationContext.Current.OutgoingResponse.Headers.Add("Access-Control-Allow-Methods", "GET");
            WebOperationContext.Current.OutgoingResponse.Headers.Add("Access-Control-Allow-Headers", "Content-Type, Accept");

            StatusCode pipeIsRunning = StatusCode.UNKNOWN;
            StatusCode pipeIsReceiving = StatusCode.UNKNOWN;
            StatusCode vsmIsRunning = StatusCode.UNKNOWN;

            pipeIsRunning = UDPListener.Instance.isRunning;

            //Checking if SSI-Pipeline is Receiving   
            pipeIsReceiving = UDPListener.Instance.isReceiving;

            //Checking if SVM is up
            vsmIsRunning = VSMHandler.getStatus();

            return new Status(pipeIsRunning, pipeIsReceiving, vsmIsRunning);

        }

        public List<String> getEventQueque()
        {
            WebOperationContext.Current.OutgoingResponse.Headers.Add("Access-Control-Allow-Origin", "*");
            WebOperationContext.Current.OutgoingResponse.Headers.Add("Access-Control-Allow-Methods", "GET");
            WebOperationContext.Current.OutgoingResponse.Headers.Add("Access-Control-Allow-Headers", "Content-Type, Accept");
                   
            return UDPListener.Instance.EventQueque;

        }

        private bool startStopPipe(int cmd)
        {

            UdpClient udpClient = new UdpClient();
            //IPEndPoint ep = new IPEndPoint(IPAddress.Parse("137.250.171.227"), 1336);
            IPEndPoint ep = new IPEndPoint(IPAddress.Parse("127.0.0.1"), 1336);
            //IPEndPoint ep = new IPEndPoint(IPAddress.Parse("137.250.171.1"), 1336);
            udpClient.Connect(ep);
            Byte[] sendBytes = new Byte[6];
            Buffer.BlockCopy(Encoding.ASCII.GetBytes("ssi"), 0, sendBytes, 0, 3);
            sendBytes[3] = (byte)cmd;
            sendBytes[4] = (byte)2;

            try
            {
                udpClient.Send(sendBytes, sendBytes.Length);
            }
            catch (Exception e)
            {
                throw e;
            }
            return true;
        }
    }
}