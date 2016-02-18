using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Text;

namespace FusionService
{

    public enum StatusCode { UP, DOWN, UNKNOWN };


    // HINWEIS: Mit dem Befehl "Umbenennen" im Menü "Umgestalten" können Sie den Schnittstellennamen "IService1" sowohl im Code als auch in der Konfigurationsdatei ändern.
    [ServiceContract]
    public interface IService1
    {

        [OperationContract]
        [WebInvoke(Method = "GET",
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare,
            UriTemplate = "getEmotion")]
        Tuple<SSIEvent[], string> getEmotion();


        [OperationContract]
        [WebInvoke(Method = "GET",
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare,
            UriTemplate = "getStatus")]
        Status getStatus();

        [OperationContract]
        [WebInvoke(Method = "GET",
            ResponseFormat = WebMessageFormat.Json,
            BodyStyle = WebMessageBodyStyle.Bare,
            UriTemplate = "getEventQueque")]
        List<String> getEventQueque();

        [OperationContract]
        [WebInvoke(Method = "POST",
           RequestFormat = WebMessageFormat.Json,
           ResponseFormat = WebMessageFormat.Json,
           BodyStyle = WebMessageBodyStyle.Bare,
           UriTemplate = "executeCommand")]

        string executeCommand(CommandData test);
    }

    [DataContract]
    public class SSIEvent
    {
        double valence = 0;
        double arousal = 0;
        string origin = "";
        string eventType = "";

        public SSIEvent(string o, double v, double a)
        {
            Origin = o;
            Valence = v;
            Arousal = a;
        }

        [DataMember]
        public double Valence
        {
            get { return valence; }
            set { valence = value; }
        }

        [DataMember]
        public double Arousal
        {
            get { return arousal; }
            set { arousal = value; }
        }

        [DataMember]
        public string Origin
        {
            get { return origin; }
            set { origin = value; }
        }

        [DataMember]
        public string EventType
        {
            get { return eventType; }
            set { eventType = value; }
        }
    }

    [DataContract]
    public class Status
    {


        StatusCode pipeIsRunning = StatusCode.UNKNOWN;
        StatusCode pipeIsReceiving = StatusCode.UNKNOWN;
        StatusCode vsmIsRunning = StatusCode.UNKNOWN;

        public Status(StatusCode piru, StatusCode pire, StatusCode svmiru)
        {
            PipeIsRunning = piru;
            PipeIsReceiving = pire;
            VsmIsRunning = svmiru;
        }

        [DataMember]
        public StatusCode PipeIsRunning
        {
            get { return pipeIsRunning; }
            set { pipeIsRunning = value; }
        }

        [DataMember]
        public StatusCode PipeIsReceiving
        {
            get { return pipeIsReceiving; }
            set { pipeIsReceiving = value; }
        }

        [DataMember]
        public StatusCode VsmIsRunning
        {
            get { return vsmIsRunning; }
            set { vsmIsRunning = value; }
        }
    }

    [DataContract]
    public class CommandData
    {


        string target = "";
        string command = "";

        public CommandData(string t, string c)
        {
            Target = t;
            Command = c;

        }

        [DataMember]
        public string Target
        {
            get { return target; }
            set { target = value; }
        }

        [DataMember]
        public string Command
        {
            get { return command; }
            set { command = value; }
        }

    }

}
