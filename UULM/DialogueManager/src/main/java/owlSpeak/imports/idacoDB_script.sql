USE [master]
GO
/****** Object:  Database [IDACO]    Script Date: 05.05.2017 16:45:53 ******/
CREATE DATABASE [IDACO]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'IDACO', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL12.SQLEXPRESS\MSSQL\DATA\IDACO.mdf' , SIZE = 5120KB , MAXSIZE = UNLIMITED, FILEGROWTH = 1024KB )
 LOG ON 
( NAME = N'IDACO_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL12.SQLEXPRESS\MSSQL\DATA\IDACO_log.ldf' , SIZE = 1024KB , MAXSIZE = 2048GB , FILEGROWTH = 10%)
GO
ALTER DATABASE [IDACO] SET COMPATIBILITY_LEVEL = 120
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [IDACO].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [IDACO] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [IDACO] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [IDACO] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [IDACO] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [IDACO] SET ARITHABORT OFF 
GO
ALTER DATABASE [IDACO] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [IDACO] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [IDACO] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [IDACO] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [IDACO] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [IDACO] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [IDACO] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [IDACO] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [IDACO] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [IDACO] SET  DISABLE_BROKER 
GO
ALTER DATABASE [IDACO] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [IDACO] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [IDACO] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [IDACO] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [IDACO] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [IDACO] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [IDACO] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [IDACO] SET RECOVERY SIMPLE 
GO
ALTER DATABASE [IDACO] SET  MULTI_USER 
GO
ALTER DATABASE [IDACO] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [IDACO] SET DB_CHAINING OFF 
GO
ALTER DATABASE [IDACO] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [IDACO] SET TARGET_RECOVERY_TIME = 0 SECONDS 
GO
ALTER DATABASE [IDACO] SET DELAYED_DURABILITY = DISABLED 
GO
USE [IDACO]
GO
/****** Object:  User [idaco]    Script Date: 05.05.2017 16:45:53 ******/
CREATE USER [idaco] FOR LOGIN [idaco] WITH DEFAULT_SCHEMA=[dbo]
GO
ALTER ROLE [db_owner] ADD MEMBER [idaco]
GO
/****** Object:  Table [dbo].[_old_IDACO_devices]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[_old_IDACO_devices](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[ParentID] [int] NULL,
	[Name] [nvarchar](max) NOT NULL,
	[NumParam] [real] NULL,
	[StringParam] [nvarchar](max) NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[IDACO_configuration]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[IDACO_configuration](
	[configuration_id] [int] IDENTITY(1,1) NOT NULL,
	[configuration_key] [nvarchar](50) NOT NULL,
	[configuration_value] [nvarchar](max) NOT NULL,
 CONSTRAINT [PK_IDACO_configuration] PRIMARY KEY CLUSTERED 
(
	[configuration_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[IDACO_device]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[IDACO_device](
	[device_list_id] [int] IDENTITY(1,1) NOT NULL,
	[device_type_id] [int] NULL,
	[device_name] [nvarchar](50) NOT NULL,
	[device_manufacturer] [nvarchar](50) NOT NULL,
	[device_description] [nvarchar](50) NOT NULL,
	[device_serial] [nvarchar](50) NULL,
	[device_mac] [nvarchar](17) NULL,
	[device_com_bus_id] [int] NULL,
 CONSTRAINT [PK_IDACO_device] PRIMARY KEY CLUSTERED 
(
	[device_list_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[IDACO_device_parameter_list]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[IDACO_device_parameter_list](
	[device_parameter_list_id] [int] IDENTITY(1,1) NOT NULL,
	[device_parameter_name] [nvarchar](50) NOT NULL,
	[device_parameter_id] [int] NOT NULL,
	[device_list_id] [int] NOT NULL,
 CONSTRAINT [PK_IDACO_device_parameter_list] PRIMARY KEY CLUSTERED 
(
	[device_parameter_list_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[IDACO_device_status]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[IDACO_device_status](
	[device_status_id] [int] IDENTITY(1,1) NOT NULL,
	[device_list_id] [int] NOT NULL,
	[session_id] [int] NULL,
	[device_status_param_key] [int] NOT NULL,
	[device_status_param_value] [float] NOT NULL,
	[device_status_datetime] [datetime] NOT NULL,
 CONSTRAINT [PK_IDACO_device_status] PRIMARY KEY CLUSTERED 
(
	[device_status_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[IDACO_device_status_setup]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[IDACO_device_status_setup](
	[device_status_setup_id] [int] IDENTITY(1,1) NOT NULL,
	[device_list_id] [int] NOT NULL,
	[setup_id] [int] NOT NULL,
	[device_status_param_key] [int] NOT NULL,
	[device_status_param_value] [float] NOT NULL,
 CONSTRAINT [PK_IDACO_device_status_setup] PRIMARY KEY CLUSTERED 
(
	[device_status_setup_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[IDACO_device_status_target]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[IDACO_device_status_target](
	[device_status_target_id] [int] IDENTITY(1,1) NOT NULL,
	[device_list_id] [int] NOT NULL,
	[session_id] [int] NULL,
	[device_status_param_key] [int] NOT NULL,
	[device_status_param_value] [float] NOT NULL,
	[device_status_datetime] [datetime] NOT NULL,
 CONSTRAINT [PK_IDACO_device_status_target] PRIMARY KEY CLUSTERED 
(
	[device_status_target_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[IDACO_device_type]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[IDACO_device_type](
	[device_type_id] [int] IDENTITY(1,1) NOT NULL,
	[device_type_name] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_IDACO_device_type] PRIMARY KEY CLUSTERED 
(
	[device_type_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[IDACO_gender_value]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[IDACO_gender_value](
	[patient_gender] [int] NOT NULL,
	[patient_gender_value] [nchar](10) NOT NULL,
 CONSTRAINT [PK_IDACO_gender_value] PRIMARY KEY CLUSTERED 
(
	[patient_gender] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[IDACO_laboratory_data]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[IDACO_laboratory_data](
	[laboratory_data_id] [int] IDENTITY(1,1) NOT NULL,
	[laboratory_data_name] [nvarchar](50) NOT NULL,
	[laboratory_data_description] [nvarchar](max) NOT NULL,
	[SI_unit_id] [int] NOT NULL,
 CONSTRAINT [PK_IDACO_laboratory_data] PRIMARY KEY CLUSTERED 
(
	[laboratory_data_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[IDACO_laboratory_data_list]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[IDACO_laboratory_data_list](
	[laboratory_data_list_id] [bigint] IDENTITY(1,1) NOT NULL,
	[record_id] [int] NOT NULL,
	[laboratory_data_id] [int] NOT NULL,
	[laboratory_data_value] [decimal](18, 5) NOT NULL
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[IDACO_medications]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[IDACO_medications](
	[medication_id] [int] IDENTITY(1,1) NOT NULL,
	[medication_name] [nvarchar](50) NOT NULL,
	[medication_description] [nvarchar](max) NULL,
	[SI_unit_id] [int] NOT NULL,
 CONSTRAINT [PK_IDACO_medications] PRIMARY KEY CLUSTERED 
(
	[medication_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[IDACO_patient]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[IDACO_patient](
	[patient_id] [int] IDENTITY(1,1) NOT NULL,
	[patient_gender] [int] NULL,
	[patient_name] [nvarchar](50) NOT NULL,
	[patient_surname] [nvarchar](50) NOT NULL,
	[patient_birthdate] [date] NOT NULL,
	[patient_height] [int] NULL,
	[patient_weight] [int] NULL,
 CONSTRAINT [PK_IDACO_patient] PRIMARY KEY CLUSTERED 
(
	[patient_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[IDACO_patient_pre_disease_list]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[IDACO_patient_pre_disease_list](
	[pre_disease_list_id] [bigint] IDENTITY(1,1) NOT NULL,
	[record_id] [int] NOT NULL,
	[pre_disease_id] [int] NOT NULL,
	[pre_disease_type_id] [int] NOT NULL,
	[pre_disease_occourence_year] [int] NOT NULL,
	[pre_disease_comment] [varchar](50) NULL
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[IDACO_patient_pre_medication_list]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[IDACO_patient_pre_medication_list](
	[pre_medication_list_id] [bigint] IDENTITY(1,1) NOT NULL,
	[record_id] [int] NOT NULL,
	[medication_id] [int] NOT NULL,
	[pre_medication_dosis_per_day] [decimal](18, 5) NOT NULL,
	[pre_medication_comment] [nvarchar](max) NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[IDACO_pre_disease]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[IDACO_pre_disease](
	[pre_disease_id] [int] IDENTITY(1,1) NOT NULL,
	[pre_disease_name] [nvarchar](max) NOT NULL,
	[pre_disease_shortcut] [nvarchar](10) NOT NULL,
 CONSTRAINT [PK_IDACO_pre_disease] PRIMARY KEY CLUSTERED 
(
	[pre_disease_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[IDACO_pre_disease_type]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[IDACO_pre_disease_type](
	[pre_disease_type_id] [int] IDENTITY(1,1) NOT NULL,
	[pre_disease_type_name] [nvarchar](50) NOT NULL,
	[pre_disease_type_description] [nvarchar](max) NOT NULL,
 CONSTRAINT [PK_IDACO_pre_disease_type] PRIMARY KEY CLUSTERED 
(
	[pre_disease_type_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[IDACO_session]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[IDACO_session](
	[session_id] [int] IDENTITY(1,1) NOT NULL,
	[session_datetime] [datetime] NOT NULL,
	[session_case_number] [nvarchar](20) NOT NULL,
	[session_type_id] [int] NOT NULL,
	[patient_id] [int] NOT NULL,
	[primary_surgeon_id] [int] NOT NULL,
	[secondary_surgeon_id] [int] NULL,
	[session_comment] [nvarchar](max) NULL,
 CONSTRAINT [PK_IDACO_session] PRIMARY KEY CLUSTERED 
(
	[session_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[IDACO_session_record]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[IDACO_session_record](
	[record_id] [int] IDENTITY(1,1) NOT NULL,
	[patient_id] [int] NOT NULL,
	[session_id] [int] NOT NULL,
	[hospital_admission_date] [datetime] NULL,
	[urgency_level_id] [int] NOT NULL,
	[record_comment] [nvarchar](max) NULL,
 CONSTRAINT [PK_IDACO_session_record] PRIMARY KEY CLUSTERED 
(
	[record_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[IDACO_session_team]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[IDACO_session_team](
	[session_team_id] [int] IDENTITY(1,1) NOT NULL,
	[session_id] [int] NOT NULL,
	[staff_id] [int] NOT NULL,
	[staff_is_present] [bit] NULL,
	[datetime_last_change] [datetime] NULL,
 CONSTRAINT [PK_IDACO_session_team] PRIMARY KEY CLUSTERED 
(
	[session_team_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[IDACO_session_type]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[IDACO_session_type](
	[session_type_id] [int] IDENTITY(1,1) NOT NULL,
	[session_type_name] [nvarchar](50) NOT NULL,
	[session_type_description] [nvarchar](500) NULL,
 CONSTRAINT [PK_IDACO_session_type] PRIMARY KEY CLUSTERED 
(
	[session_type_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[IDACO_setup]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[IDACO_setup](
	[setup_id] [int] IDENTITY(1,1) NOT NULL,
	[setup_name] [nvarchar](max) NOT NULL,
	[session_type_id] [int] NULL,
 CONSTRAINT [PK_IDACO_setup] PRIMARY KEY CLUSTERED 
(
	[setup_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[IDACO_setup_device]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[IDACO_setup_device](
	[setup_device_id] [int] IDENTITY(1,1) NOT NULL,
	[setup_id] [int] NOT NULL,
	[device_id] [int] NOT NULL,
 CONSTRAINT [PK_IDACO_setup_device] PRIMARY KEY CLUSTERED 
(
	[setup_device_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[IDACO_SI_unit]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[IDACO_SI_unit](
	[SI_unit_id] [int] IDENTITY(1,1) NOT NULL,
	[SI_unit_type_shortcut] [nvarchar](20) NOT NULL,
	[SI_unit_type] [nvarchar](50) NOT NULL,
	[SI_unit_usage] [nvarchar](max) NULL,
 CONSTRAINT [PK_IDACO_SI_unit] PRIMARY KEY CLUSTERED 
(
	[SI_unit_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[IDACO_staff]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[IDACO_staff](
	[staff_id] [int] IDENTITY(1,1) NOT NULL,
	[staff_role_id] [int] NOT NULL,
	[staff_title] [nchar](20) NULL,
	[staff_name] [nvarchar](50) NULL,
	[staff_surname] [nvarchar](50) NOT NULL,
	[staff_experience] [int] NOT NULL,
 CONSTRAINT [PK_IDACO_staff] PRIMARY KEY CLUSTERED 
(
	[staff_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[IDACO_staff_role]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[IDACO_staff_role](
	[staff_role_id] [int] IDENTITY(1,1) NOT NULL,
	[staff_role_name] [nvarchar](50) NOT NULL,
	[staff_role_description] [nvarchar](max) NOT NULL,
 CONSTRAINT [PK_IDACO_staff_role] PRIMARY KEY CLUSTERED 
(
	[staff_role_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[IDACO_staff_setup]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[IDACO_staff_setup](
	[staff_setup_id] [int] IDENTITY(1,1) NOT NULL,
	[staff_id] [int] NOT NULL,
	[setup_id] [int] NOT NULL,
	[setup_active] [bit] NULL,
 CONSTRAINT [PK_IDACO_staff_setup] PRIMARY KEY CLUSTERED 
(
	[staff_setup_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[IDACO_urgency_level]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[IDACO_urgency_level](
	[urgency_level_id] [int] IDENTITY(1,1) NOT NULL,
	[urgency_level_name] [nvarchar](30) NOT NULL,
	[urgency_level_description] [nvarchar](max) NULL,
 CONSTRAINT [PK_IDACO_urgency_level] PRIMARY KEY CLUSTERED 
(
	[urgency_level_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
SET IDENTITY_INSERT [dbo].[_old_IDACO_devices] ON 

INSERT [dbo].[_old_IDACO_devices] ([ID], [ParentID], [Name], [NumParam], [StringParam]) VALUES (1, NULL, N'Device', NULL, N'RoomLight')
INSERT [dbo].[_old_IDACO_devices] ([ID], [ParentID], [Name], [NumParam], [StringParam]) VALUES (2, 1, N'LightStatus', 0, NULL)
INSERT [dbo].[_old_IDACO_devices] ([ID], [ParentID], [Name], [NumParam], [StringParam]) VALUES (3, NULL, N'Device', NULL, N'Thermoflator')
INSERT [dbo].[_old_IDACO_devices] ([ID], [ParentID], [Name], [NumParam], [StringParam]) VALUES (4, 3, N'TargetPressure', 17, NULL)
INSERT [dbo].[_old_IDACO_devices] ([ID], [ParentID], [Name], [NumParam], [StringParam]) VALUES (5, 3, N'CurrentPressure', 5, NULL)
INSERT [dbo].[_old_IDACO_devices] ([ID], [ParentID], [Name], [NumParam], [StringParam]) VALUES (6, 3, N'TargetFlow', 20, NULL)
INSERT [dbo].[_old_IDACO_devices] ([ID], [ParentID], [Name], [NumParam], [StringParam]) VALUES (7, 3, N'CurrentFlow', 18, NULL)
INSERT [dbo].[_old_IDACO_devices] ([ID], [ParentID], [Name], [NumParam], [StringParam]) VALUES (8, 3, N'CylinderPressure', 0, NULL)
INSERT [dbo].[_old_IDACO_devices] ([ID], [ParentID], [Name], [NumParam], [StringParam]) VALUES (9, 3, N'GasTemperature', 0, NULL)
INSERT [dbo].[_old_IDACO_devices] ([ID], [ParentID], [Name], [NumParam], [StringParam]) VALUES (10, 3, N'GasVolume', 0, NULL)
INSERT [dbo].[_old_IDACO_devices] ([ID], [ParentID], [Name], [NumParam], [StringParam]) VALUES (11, NULL, N'Device', NULL, N'LapLight')
INSERT [dbo].[_old_IDACO_devices] ([ID], [ParentID], [Name], [NumParam], [StringParam]) VALUES (12, 11, N'Intensity', 70, NULL)
INSERT [dbo].[_old_IDACO_devices] ([ID], [ParentID], [Name], [NumParam], [StringParam]) VALUES (13, 11, N'TargetIntensity', 70, NULL)
INSERT [dbo].[_old_IDACO_devices] ([ID], [ParentID], [Name], [NumParam], [StringParam]) VALUES (14, NULL, N'Device', NULL, N'Endomat')
INSERT [dbo].[_old_IDACO_devices] ([ID], [ParentID], [Name], [NumParam], [StringParam]) VALUES (15, 14, N'TargetFlow', 500, NULL)
INSERT [dbo].[_old_IDACO_devices] ([ID], [ParentID], [Name], [NumParam], [StringParam]) VALUES (16, NULL, N'Device', NULL, N'OR-Table')
INSERT [dbo].[_old_IDACO_devices] ([ID], [ParentID], [Name], [NumParam], [StringParam]) VALUES (17, 16, N'Height', 130, NULL)
INSERT [dbo].[_old_IDACO_devices] ([ID], [ParentID], [Name], [NumParam], [StringParam]) VALUES (18, 16, N'RevTrend', 20, NULL)
INSERT [dbo].[_old_IDACO_devices] ([ID], [ParentID], [Name], [NumParam], [StringParam]) VALUES (19, 16, N'TiltRight', 3, NULL)
SET IDENTITY_INSERT [dbo].[_old_IDACO_devices] OFF
SET IDENTITY_INSERT [dbo].[IDACO_configuration] ON 

INSERT [dbo].[IDACO_configuration] ([configuration_id], [configuration_key], [configuration_value]) VALUES (1, N'IDACO_CURRENT_SESSION', N'2')
SET IDENTITY_INSERT [dbo].[IDACO_configuration] OFF
SET IDENTITY_INSERT [dbo].[IDACO_device] ON 

INSERT [dbo].[IDACO_device] ([device_list_id], [device_type_id], [device_name], [device_manufacturer], [device_description], [device_serial], [device_mac], [device_com_bus_id]) VALUES (3, 2, N'VIO 300 D', N'ERBE', N'4 Buchsen Modul', NULL, NULL, NULL)
INSERT [dbo].[IDACO_device] ([device_list_id], [device_type_id], [device_name], [device_manufacturer], [device_description], [device_serial], [device_mac], [device_com_bus_id]) VALUES (4, 4, N'Tricam SL NTSC', N'Karl Storz', N'3 Chip Endo Cam', NULL, NULL, NULL)
INSERT [dbo].[IDACO_device] ([device_list_id], [device_type_id], [device_name], [device_manufacturer], [device_description], [device_serial], [device_mac], [device_com_bus_id]) VALUES (5, 3, N'UCR', N'Olympus', N'CO2-Insufflator', NULL, NULL, NULL)
INSERT [dbo].[IDACO_device] ([device_list_id], [device_type_id], [device_name], [device_manufacturer], [device_description], [device_serial], [device_mac], [device_com_bus_id]) VALUES (6, 6, N'TruSystem 7500', N'Trumpf Medical', N'OP-Tisch / Systemtisch', NULL, NULL, NULL)
INSERT [dbo].[IDACO_device] ([device_list_id], [device_type_id], [device_name], [device_manufacturer], [device_description], [device_serial], [device_mac], [device_com_bus_id]) VALUES (7, 5, N'iLED 7', N'Trumpf Medical', N'OP Leuchte', NULL, NULL, NULL)
INSERT [dbo].[IDACO_device] ([device_list_id], [device_type_id], [device_name], [device_manufacturer], [device_description], [device_serial], [device_mac], [device_com_bus_id]) VALUES (8, 7, N'Zeus Infinity', N'Dräger', N'Anästhesie Arbeitsplatz', NULL, NULL, NULL)
INSERT [dbo].[IDACO_device] ([device_list_id], [device_type_id], [device_name], [device_manufacturer], [device_description], [device_serial], [device_mac], [device_com_bus_id]) VALUES (10, 4, N'Endoskopie-Turm', N'Karl Storz', N'Endoskopie-Turm OP7/8 (Storz)', NULL, NULL, NULL)
INSERT [dbo].[IDACO_device] ([device_list_id], [device_type_id], [device_name], [device_manufacturer], [device_description], [device_serial], [device_mac], [device_com_bus_id]) VALUES (12, 4, N'Endoskopie-Turm', N'Olympus', N'Endoskopie-Turm OP7/8 (Olympus)', NULL, NULL, NULL)
INSERT [dbo].[IDACO_device] ([device_list_id], [device_type_id], [device_name], [device_manufacturer], [device_description], [device_serial], [device_mac], [device_com_bus_id]) VALUES (14, 8, N'Lap. Ultraschall ', N'nA', N'Lap. Ultraschall Panther 202', NULL, NULL, NULL)
INSERT [dbo].[IDACO_device] ([device_list_id], [device_type_id], [device_name], [device_manufacturer], [device_description], [device_serial], [device_mac], [device_com_bus_id]) VALUES (15, 9, N'C-Bogen', N'GE', N'C-Bogen OEC 9800', NULL, NULL, NULL)
INSERT [dbo].[IDACO_device] ([device_list_id], [device_type_id], [device_name], [device_manufacturer], [device_description], [device_serial], [device_mac], [device_com_bus_id]) VALUES (16, 2, N'Harmonic Ultrascission (1)', N'Ethicon', N'Harmonic Ultrascission', NULL, NULL, NULL)
INSERT [dbo].[IDACO_device] ([device_list_id], [device_type_id], [device_name], [device_manufacturer], [device_description], [device_serial], [device_mac], [device_com_bus_id]) VALUES (19, 2, N'Harmonic Ultrascission (2)', N'Ethicon', N'Harmoic Ultrascission', NULL, NULL, NULL)
INSERT [dbo].[IDACO_device] ([device_list_id], [device_type_id], [device_name], [device_manufacturer], [device_description], [device_serial], [device_mac], [device_com_bus_id]) VALUES (20, 10, N'MIC-Turm (OP7)', N'Karl Storz', N'Laparoskopie Turm', NULL, NULL, NULL)
INSERT [dbo].[IDACO_device] ([device_list_id], [device_type_id], [device_name], [device_manufacturer], [device_description], [device_serial], [device_mac], [device_com_bus_id]) VALUES (21, 8, N'Ultraschall (offen)', N'nA', N'Ultraschall Gerät', NULL, NULL, NULL)
INSERT [dbo].[IDACO_device] ([device_list_id], [device_type_id], [device_name], [device_manufacturer], [device_description], [device_serial], [device_mac], [device_com_bus_id]) VALUES (22, 2, N'Valley Lab (HF)', N'Valley Lab', N'Valley Lab HF-Generator', NULL, NULL, NULL)
INSERT [dbo].[IDACO_device] ([device_list_id], [device_type_id], [device_name], [device_manufacturer], [device_description], [device_serial], [device_mac], [device_com_bus_id]) VALUES (23, 11, N'Argon Laser', N'nA', N'Argon Laser', NULL, NULL, NULL)
INSERT [dbo].[IDACO_device] ([device_list_id], [device_type_id], [device_name], [device_manufacturer], [device_description], [device_serial], [device_mac], [device_com_bus_id]) VALUES (24, 12, N'RITA', N'angiodynamics', N'RITA RF Generator', NULL, NULL, NULL)
INSERT [dbo].[IDACO_device] ([device_list_id], [device_type_id], [device_name], [device_manufacturer], [device_description], [device_serial], [device_mac], [device_com_bus_id]) VALUES (25, 13, N'Power LED 175', N'Karl Storz', N'Laparoskopische Lichtquelle (LED)', NULL, NULL, 4110)
INSERT [dbo].[IDACO_device] ([device_list_id], [device_type_id], [device_name], [device_manufacturer], [device_description], [device_serial], [device_mac], [device_com_bus_id]) VALUES (26, 13, N'Xenon 300', N'Karl Storz', N'Laparoskopische Lichtquelle (Xenon)', NULL, NULL, 4098)
INSERT [dbo].[IDACO_device] ([device_list_id], [device_type_id], [device_name], [device_manufacturer], [device_description], [device_serial], [device_mac], [device_com_bus_id]) VALUES (27, 3, N'Thermoflator', N'Karl Storz', N'CO2-Insufflator', NULL, NULL, 1)
INSERT [dbo].[IDACO_device] ([device_list_id], [device_type_id], [device_name], [device_manufacturer], [device_description], [device_serial], [device_mac], [device_com_bus_id]) VALUES (28, 14, N'Endomat LC', N'Karl Storz', N'Saug-Spuel-Einheit', NULL, NULL, 3076)
INSERT [dbo].[IDACO_device] ([device_list_id], [device_type_id], [device_name], [device_manufacturer], [device_description], [device_serial], [device_mac], [device_com_bus_id]) VALUES (31, 14, N'Endomat LC', N'Karl Storz', N'Saug-Spuel-Einheit', NULL, NULL, 1284)
INSERT [dbo].[IDACO_device] ([device_list_id], [device_type_id], [device_name], [device_manufacturer], [device_description], [device_serial], [device_mac], [device_com_bus_id]) VALUES (32, 14, N'Hamou Endomat', N'Karl Storz', N'Saug-Spuel-Einheit', NULL, NULL, 3072)
SET IDENTITY_INSERT [dbo].[IDACO_device] OFF
SET IDENTITY_INSERT [dbo].[IDACO_device_parameter_list] ON 

INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (1, N'Intensity', 2, 25)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (2, N'TargetIntensity', 5, 25)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (3, N'status', 0, 25)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (4, N'Intensity', 2, 26)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (5, N'TargetIntensity', 5, 26)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (6, N'status', 0, 26)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (7, N'TargetPressure', 1, 27)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (8, N'CurrentPressure', 2, 27)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (9, N'TargetFlow', 3, 27)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (10, N'CurrentFlow', 4, 27)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (11, N'CylinderPressure', 5, 27)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (12, N'GasTemperature', 6, 27)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (13, N'GasVolume', 7, 27)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (14, N'TargetValue-F', 1, 28)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (15, N'TargetValue-N', 7, 28)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (16, N'TargetValue-H', 8, 28)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (17, N'TargetValue-F', 1, 31)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (18, N'TargetValue-N', 7, 31)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (19, N'TargetValue-H', 8, 31)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (20, N'TargetFlow', 1, 32)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (21, N'CurrentFlow', 2, 32)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (22, N'TargetPressure', 3, 32)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (23, N'CurrentPressure', 4, 32)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (24, N'TargetSuction', 5, 32)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (25, N'CurrentSuction', 6, 32)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (26, N'IrrigationVolume', 7, 32)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (27, N'Status-Thermoflator', 0, 27)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (28, N'Status-Endomat_1', 0, 28)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (29, N'Status-Endomat_2', 0, 31)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (31, N'Status-Hamou', 0, 32)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (32, N'TableHeight', 1, 6)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (33, N'TableTilt', 2, 6)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (34, N'LightIntensity', 1, 7)
INSERT [dbo].[IDACO_device_parameter_list] ([device_parameter_list_id], [device_parameter_name], [device_parameter_id], [device_list_id]) VALUES (35, N'ColorTemperature', 2, 7)
SET IDENTITY_INSERT [dbo].[IDACO_device_parameter_list] OFF
SET IDENTITY_INSERT [dbo].[IDACO_device_status] ON 

INSERT [dbo].[IDACO_device_status] ([device_status_id], [device_list_id], [session_id], [device_status_param_key], [device_status_param_value], [device_status_datetime]) VALUES (1, 31, NULL, 29, 0, CAST(N'2017-03-28 15:11:19.573' AS DateTime))
INSERT [dbo].[IDACO_device_status] ([device_status_id], [device_list_id], [session_id], [device_status_param_key], [device_status_param_value], [device_status_datetime]) VALUES (2, 31, NULL, 19, 500, CAST(N'2017-03-28 15:11:07.637' AS DateTime))
INSERT [dbo].[IDACO_device_status] ([device_status_id], [device_list_id], [session_id], [device_status_param_key], [device_status_param_value], [device_status_datetime]) VALUES (3, 27, NULL, 27, 16, CAST(N'2017-03-28 15:11:05.897' AS DateTime))
INSERT [dbo].[IDACO_device_status] ([device_status_id], [device_list_id], [session_id], [device_status_param_key], [device_status_param_value], [device_status_datetime]) VALUES (4, 32, NULL, 31, 32, CAST(N'2017-03-28 15:11:05.907' AS DateTime))
INSERT [dbo].[IDACO_device_status] ([device_status_id], [device_list_id], [session_id], [device_status_param_key], [device_status_param_value], [device_status_datetime]) VALUES (5, 27, NULL, 9, 0, CAST(N'2017-05-05 16:44:34.653' AS DateTime))
INSERT [dbo].[IDACO_device_status] ([device_status_id], [device_list_id], [session_id], [device_status_param_key], [device_status_param_value], [device_status_datetime]) VALUES (6, 27, NULL, 7, 16, CAST(N'2017-05-05 16:44:34.653' AS DateTime))
INSERT [dbo].[IDACO_device_status] ([device_status_id], [device_list_id], [session_id], [device_status_param_key], [device_status_param_value], [device_status_datetime]) VALUES (7, 31, NULL, 18, 0, CAST(N'2017-03-28 15:11:19.573' AS DateTime))
INSERT [dbo].[IDACO_device_status] ([device_status_id], [device_list_id], [session_id], [device_status_param_key], [device_status_param_value], [device_status_datetime]) VALUES (8, 31, NULL, 17, 0, CAST(N'2017-05-05 16:00:34.927' AS DateTime))
INSERT [dbo].[IDACO_device_status] ([device_status_id], [device_list_id], [session_id], [device_status_param_key], [device_status_param_value], [device_status_datetime]) VALUES (9, 32, NULL, 20, 100, CAST(N'2017-03-28 15:12:19.347' AS DateTime))
INSERT [dbo].[IDACO_device_status] ([device_status_id], [device_list_id], [session_id], [device_status_param_key], [device_status_param_value], [device_status_datetime]) VALUES (10, 32, NULL, 24, 0.30000001192092896, CAST(N'2017-03-28 15:12:22.433' AS DateTime))
INSERT [dbo].[IDACO_device_status] ([device_status_id], [device_list_id], [session_id], [device_status_param_key], [device_status_param_value], [device_status_datetime]) VALUES (11, 32, NULL, 22, 60, CAST(N'2017-03-28 15:12:24.357' AS DateTime))
INSERT [dbo].[IDACO_device_status] ([device_status_id], [device_list_id], [session_id], [device_status_param_key], [device_status_param_value], [device_status_datetime]) VALUES (14, 6, NULL, 32, 30, CAST(N'2017-05-05 16:44:34.660' AS DateTime))
INSERT [dbo].[IDACO_device_status] ([device_status_id], [device_list_id], [session_id], [device_status_param_key], [device_status_param_value], [device_status_datetime]) VALUES (15, 6, NULL, 33, 0, CAST(N'2017-05-05 16:44:34.660' AS DateTime))
INSERT [dbo].[IDACO_device_status] ([device_status_id], [device_list_id], [session_id], [device_status_param_key], [device_status_param_value], [device_status_datetime]) VALUES (18, 26, NULL, 6, 0, CAST(N'2017-05-05 16:44:34.660' AS DateTime))
INSERT [dbo].[IDACO_device_status] ([device_status_id], [device_list_id], [session_id], [device_status_param_key], [device_status_param_value], [device_status_datetime]) VALUES (21, 27, NULL, 8, 0, CAST(N'2017-05-05 16:44:34.650' AS DateTime))
INSERT [dbo].[IDACO_device_status] ([device_status_id], [device_list_id], [session_id], [device_status_param_key], [device_status_param_value], [device_status_datetime]) VALUES (22, 27, NULL, 10, 0, CAST(N'2017-05-05 16:44:34.650' AS DateTime))
INSERT [dbo].[IDACO_device_status] ([device_status_id], [device_list_id], [session_id], [device_status_param_key], [device_status_param_value], [device_status_datetime]) VALUES (27, 26, NULL, 4, 50, CAST(N'2017-05-05 16:44:34.657' AS DateTime))
INSERT [dbo].[IDACO_device_status] ([device_status_id], [device_list_id], [session_id], [device_status_param_key], [device_status_param_value], [device_status_datetime]) VALUES (28, 26, NULL, 5, 70, CAST(N'2017-05-05 16:44:34.657' AS DateTime))
INSERT [dbo].[IDACO_device_status] ([device_status_id], [device_list_id], [session_id], [device_status_param_key], [device_status_param_value], [device_status_datetime]) VALUES (29, 28, NULL, 14, 0, CAST(N'2017-05-05 16:44:34.660' AS DateTime))
SET IDENTITY_INSERT [dbo].[IDACO_device_status] OFF
SET IDENTITY_INSERT [dbo].[IDACO_device_status_setup] ON 

INSERT [dbo].[IDACO_device_status_setup] ([device_status_setup_id], [device_list_id], [setup_id], [device_status_param_key], [device_status_param_value]) VALUES (1, 26, 1, 5, 70)
INSERT [dbo].[IDACO_device_status_setup] ([device_status_setup_id], [device_list_id], [setup_id], [device_status_param_key], [device_status_param_value]) VALUES (2, 6, 1, 32, 25)
INSERT [dbo].[IDACO_device_status_setup] ([device_status_setup_id], [device_list_id], [setup_id], [device_status_param_key], [device_status_param_value]) VALUES (3, 6, 1, 33, 0)
INSERT [dbo].[IDACO_device_status_setup] ([device_status_setup_id], [device_list_id], [setup_id], [device_status_param_key], [device_status_param_value]) VALUES (4, 28, 1, 14, 600)
INSERT [dbo].[IDACO_device_status_setup] ([device_status_setup_id], [device_list_id], [setup_id], [device_status_param_key], [device_status_param_value]) VALUES (5, 27, 1, 7, 15)
INSERT [dbo].[IDACO_device_status_setup] ([device_status_setup_id], [device_list_id], [setup_id], [device_status_param_key], [device_status_param_value]) VALUES (6, 27, 1, 9, 18)
INSERT [dbo].[IDACO_device_status_setup] ([device_status_setup_id], [device_list_id], [setup_id], [device_status_param_key], [device_status_param_value]) VALUES (7, 26, 2, 5, 80)
INSERT [dbo].[IDACO_device_status_setup] ([device_status_setup_id], [device_list_id], [setup_id], [device_status_param_key], [device_status_param_value]) VALUES (8, 6, 2, 32, 40)
INSERT [dbo].[IDACO_device_status_setup] ([device_status_setup_id], [device_list_id], [setup_id], [device_status_param_key], [device_status_param_value]) VALUES (9, 6, 2, 33, -25)
INSERT [dbo].[IDACO_device_status_setup] ([device_status_setup_id], [device_list_id], [setup_id], [device_status_param_key], [device_status_param_value]) VALUES (10, 28, 2, 14, 670)
INSERT [dbo].[IDACO_device_status_setup] ([device_status_setup_id], [device_list_id], [setup_id], [device_status_param_key], [device_status_param_value]) VALUES (11, 27, 2, 7, 15)
INSERT [dbo].[IDACO_device_status_setup] ([device_status_setup_id], [device_list_id], [setup_id], [device_status_param_key], [device_status_param_value]) VALUES (12, 27, 2, 9, 20)
INSERT [dbo].[IDACO_device_status_setup] ([device_status_setup_id], [device_list_id], [setup_id], [device_status_param_key], [device_status_param_value]) VALUES (13, 26, 3, 5, 80)
INSERT [dbo].[IDACO_device_status_setup] ([device_status_setup_id], [device_list_id], [setup_id], [device_status_param_key], [device_status_param_value]) VALUES (14, 27, 3, 7, 15)
INSERT [dbo].[IDACO_device_status_setup] ([device_status_setup_id], [device_list_id], [setup_id], [device_status_param_key], [device_status_param_value]) VALUES (15, 27, 3, 9, 20)
INSERT [dbo].[IDACO_device_status_setup] ([device_status_setup_id], [device_list_id], [setup_id], [device_status_param_key], [device_status_param_value]) VALUES (16, 6, 3, 32, 40)
INSERT [dbo].[IDACO_device_status_setup] ([device_status_setup_id], [device_list_id], [setup_id], [device_status_param_key], [device_status_param_value]) VALUES (17, 6, 3, 33, -25)
INSERT [dbo].[IDACO_device_status_setup] ([device_status_setup_id], [device_list_id], [setup_id], [device_status_param_key], [device_status_param_value]) VALUES (18, 31, 3, 17, 670)
SET IDENTITY_INSERT [dbo].[IDACO_device_status_setup] OFF
SET IDENTITY_INSERT [dbo].[IDACO_device_status_target] ON 

INSERT [dbo].[IDACO_device_status_target] ([device_status_target_id], [device_list_id], [session_id], [device_status_param_key], [device_status_param_value], [device_status_datetime]) VALUES (1, 27, NULL, 1, 16, CAST(N'2017-03-29 10:34:54.663' AS DateTime))
SET IDENTITY_INSERT [dbo].[IDACO_device_status_target] OFF
SET IDENTITY_INSERT [dbo].[IDACO_device_type] ON 

INSERT [dbo].[IDACO_device_type] ([device_type_id], [device_type_name]) VALUES (1, N'Unbekannt')
INSERT [dbo].[IDACO_device_type] ([device_type_id], [device_type_name]) VALUES (2, N'HF Chirurgie')
INSERT [dbo].[IDACO_device_type] ([device_type_id], [device_type_name]) VALUES (3, N'Insufflator')
INSERT [dbo].[IDACO_device_type] ([device_type_id], [device_type_name]) VALUES (4, N'Endoskopie Turm')
INSERT [dbo].[IDACO_device_type] ([device_type_id], [device_type_name]) VALUES (5, N'OP Leuchte')
INSERT [dbo].[IDACO_device_type] ([device_type_id], [device_type_name]) VALUES (6, N'OP Tisch')
INSERT [dbo].[IDACO_device_type] ([device_type_id], [device_type_name]) VALUES (7, N'Anästhesie Arbeitsplatz')
INSERT [dbo].[IDACO_device_type] ([device_type_id], [device_type_name]) VALUES (8, N'Ultraschall')
INSERT [dbo].[IDACO_device_type] ([device_type_id], [device_type_name]) VALUES (9, N'C-Bogen')
INSERT [dbo].[IDACO_device_type] ([device_type_id], [device_type_name]) VALUES (10, N'Laparoskopie Turm')
INSERT [dbo].[IDACO_device_type] ([device_type_id], [device_type_name]) VALUES (11, N'Laser')
INSERT [dbo].[IDACO_device_type] ([device_type_id], [device_type_name]) VALUES (12, N'RF Elektro-Chirurgie')
INSERT [dbo].[IDACO_device_type] ([device_type_id], [device_type_name]) VALUES (13, N'Lichtquelle')
INSERT [dbo].[IDACO_device_type] ([device_type_id], [device_type_name]) VALUES (14, N'Saug-Spuel-Einheit')
SET IDENTITY_INSERT [dbo].[IDACO_device_type] OFF
INSERT [dbo].[IDACO_gender_value] ([patient_gender], [patient_gender_value]) VALUES (0, N'female    ')
INSERT [dbo].[IDACO_gender_value] ([patient_gender], [patient_gender_value]) VALUES (1, N'male      ')
SET IDENTITY_INSERT [dbo].[IDACO_laboratory_data] ON 

INSERT [dbo].[IDACO_laboratory_data] ([laboratory_data_id], [laboratory_data_name], [laboratory_data_description], [SI_unit_id]) VALUES (1, N'sodium', N'Natrium', 7)
INSERT [dbo].[IDACO_laboratory_data] ([laboratory_data_id], [laboratory_data_name], [laboratory_data_description], [SI_unit_id]) VALUES (2, N'potassium', N'Kalium', 7)
INSERT [dbo].[IDACO_laboratory_data] ([laboratory_data_id], [laboratory_data_name], [laboratory_data_description], [SI_unit_id]) VALUES (4, N'creatine', N'Kreatin (Kreatinin)', 8)
INSERT [dbo].[IDACO_laboratory_data] ([laboratory_data_id], [laboratory_data_name], [laboratory_data_description], [SI_unit_id]) VALUES (5, N'alkaline phosphatase', N'Alkalische Phosphatase', 9)
INSERT [dbo].[IDACO_laboratory_data] ([laboratory_data_id], [laboratory_data_name], [laboratory_data_description], [SI_unit_id]) VALUES (6, N'bilirubin', N'Bilirubin gesamt', 8)
INSERT [dbo].[IDACO_laboratory_data] ([laboratory_data_id], [laboratory_data_name], [laboratory_data_description], [SI_unit_id]) VALUES (7, N'gamma-GT', N'Gamma-GT ', 9)
INSERT [dbo].[IDACO_laboratory_data] ([laboratory_data_id], [laboratory_data_name], [laboratory_data_description], [SI_unit_id]) VALUES (9, N'GPT', N'Glutamat-Pyruvat-Transaminase (Hinweis auf Leber- und Gallenerkrankungen)', 9)
INSERT [dbo].[IDACO_laboratory_data] ([laboratory_data_id], [laboratory_data_name], [laboratory_data_description], [SI_unit_id]) VALUES (11, N'GOT', N'Glutamat-Oxalacetat-Transaminase (Hinweis auf Leber- und Gallenerkrankungen)', 9)
INSERT [dbo].[IDACO_laboratory_data] ([laboratory_data_id], [laboratory_data_name], [laboratory_data_description], [SI_unit_id]) VALUES (13, N'lipase', N'Lipase', 9)
INSERT [dbo].[IDACO_laboratory_data] ([laboratory_data_id], [laboratory_data_name], [laboratory_data_description], [SI_unit_id]) VALUES (14, N'albumin', N'Albumin', 10)
INSERT [dbo].[IDACO_laboratory_data] ([laboratory_data_id], [laboratory_data_name], [laboratory_data_description], [SI_unit_id]) VALUES (15, N'CRP', N' C-reaktives Protein (Entzündungsparameter)', 8)
INSERT [dbo].[IDACO_laboratory_data] ([laboratory_data_id], [laboratory_data_name], [laboratory_data_description], [SI_unit_id]) VALUES (16, N'prothrombin time', N'Quick-Wert', 11)
INSERT [dbo].[IDACO_laboratory_data] ([laboratory_data_id], [laboratory_data_name], [laboratory_data_description], [SI_unit_id]) VALUES (18, N'aPTT', N'Partielle Thromboplastinzeit ', 1)
INSERT [dbo].[IDACO_laboratory_data] ([laboratory_data_id], [laboratory_data_name], [laboratory_data_description], [SI_unit_id]) VALUES (20, N'leucocytes', N'Leukozyten', 12)
INSERT [dbo].[IDACO_laboratory_data] ([laboratory_data_id], [laboratory_data_name], [laboratory_data_description], [SI_unit_id]) VALUES (21, N'haemoglobin', N'Hämoglobin', 12)
INSERT [dbo].[IDACO_laboratory_data] ([laboratory_data_id], [laboratory_data_name], [laboratory_data_description], [SI_unit_id]) VALUES (22, N'thrombocytes', N'Trombozyten', 12)
SET IDENTITY_INSERT [dbo].[IDACO_laboratory_data] OFF
SET IDENTITY_INSERT [dbo].[IDACO_laboratory_data_list] ON 

INSERT [dbo].[IDACO_laboratory_data_list] ([laboratory_data_list_id], [record_id], [laboratory_data_id], [laboratory_data_value]) VALUES (1, 3, 11, CAST(53.00000 AS Decimal(18, 5)))
INSERT [dbo].[IDACO_laboratory_data_list] ([laboratory_data_list_id], [record_id], [laboratory_data_id], [laboratory_data_value]) VALUES (2, 3, 9, CAST(104.00000 AS Decimal(18, 5)))
INSERT [dbo].[IDACO_laboratory_data_list] ([laboratory_data_list_id], [record_id], [laboratory_data_id], [laboratory_data_value]) VALUES (3, 3, 7, CAST(116.00000 AS Decimal(18, 5)))
INSERT [dbo].[IDACO_laboratory_data_list] ([laboratory_data_list_id], [record_id], [laboratory_data_id], [laboratory_data_value]) VALUES (4, 3, 15, CAST(0.20000 AS Decimal(18, 5)))
INSERT [dbo].[IDACO_laboratory_data_list] ([laboratory_data_list_id], [record_id], [laboratory_data_id], [laboratory_data_value]) VALUES (5, 3, 6, CAST(0.30000 AS Decimal(18, 5)))
INSERT [dbo].[IDACO_laboratory_data_list] ([laboratory_data_list_id], [record_id], [laboratory_data_id], [laboratory_data_value]) VALUES (6, 3, 20, CAST(6.87000 AS Decimal(18, 5)))
INSERT [dbo].[IDACO_laboratory_data_list] ([laboratory_data_list_id], [record_id], [laboratory_data_id], [laboratory_data_value]) VALUES (7, 3, 5, CAST(123.00000 AS Decimal(18, 5)))
INSERT [dbo].[IDACO_laboratory_data_list] ([laboratory_data_list_id], [record_id], [laboratory_data_id], [laboratory_data_value]) VALUES (8, 3, 16, CAST(110.00000 AS Decimal(18, 5)))
SET IDENTITY_INSERT [dbo].[IDACO_laboratory_data_list] OFF
SET IDENTITY_INSERT [dbo].[IDACO_medications] ON 

INSERT [dbo].[IDACO_medications] ([medication_id], [medication_name], [medication_description], [SI_unit_id]) VALUES (4, N'Ibuprofen', N'Schmerzmittel', 19)
INSERT [dbo].[IDACO_medications] ([medication_id], [medication_name], [medication_description], [SI_unit_id]) VALUES (5, N'Phenprocoumon ', N'Gerinnungshemmer', 19)
INSERT [dbo].[IDACO_medications] ([medication_id], [medication_name], [medication_description], [SI_unit_id]) VALUES (6, N'Aspirin', N'Schmerzmittel Blutverdünnung', 19)
SET IDENTITY_INSERT [dbo].[IDACO_medications] OFF
SET IDENTITY_INSERT [dbo].[IDACO_patient] ON 

INSERT [dbo].[IDACO_patient] ([patient_id], [patient_gender], [patient_name], [patient_surname], [patient_birthdate], [patient_height], [patient_weight]) VALUES (1, 1, N'Max', N'Mustermann', CAST(N'1976-02-01' AS Date), 185, 85)
INSERT [dbo].[IDACO_patient] ([patient_id], [patient_gender], [patient_name], [patient_surname], [patient_birthdate], [patient_height], [patient_weight]) VALUES (2, 0, N'Erika', N'Mustermann', CAST(N'1964-08-12' AS Date), 175, 65)
INSERT [dbo].[IDACO_patient] ([patient_id], [patient_gender], [patient_name], [patient_surname], [patient_birthdate], [patient_height], [patient_weight]) VALUES (3, 1, N'John', N'Doe', CAST(N'1990-10-25' AS Date), 180, 90)
SET IDENTITY_INSERT [dbo].[IDACO_patient] OFF
SET IDENTITY_INSERT [dbo].[IDACO_patient_pre_disease_list] ON 

INSERT [dbo].[IDACO_patient_pre_disease_list] ([pre_disease_list_id], [record_id], [pre_disease_id], [pre_disease_type_id], [pre_disease_occourence_year], [pre_disease_comment]) VALUES (1, 3, 5, 1, 0, N'einmalige ERCP vor 5 Monaten')
INSERT [dbo].[IDACO_patient_pre_disease_list] ([pre_disease_list_id], [record_id], [pre_disease_id], [pre_disease_type_id], [pre_disease_occourence_year], [pre_disease_comment]) VALUES (3, 4, 1, 3, 0, N'chronische Herzmuskelentzündung')
SET IDENTITY_INSERT [dbo].[IDACO_patient_pre_disease_list] OFF
SET IDENTITY_INSERT [dbo].[IDACO_patient_pre_medication_list] ON 

INSERT [dbo].[IDACO_patient_pre_medication_list] ([pre_medication_list_id], [record_id], [medication_id], [pre_medication_dosis_per_day], [pre_medication_comment]) VALUES (1, 3, 6, CAST(400.00000 AS Decimal(18, 5)), N'muss vor OP abgesetzt werden')
INSERT [dbo].[IDACO_patient_pre_medication_list] ([pre_medication_list_id], [record_id], [medication_id], [pre_medication_dosis_per_day], [pre_medication_comment]) VALUES (2, 3, 4, CAST(600.00000 AS Decimal(18, 5)), N'Verabreichung trozt OP')
INSERT [dbo].[IDACO_patient_pre_medication_list] ([pre_medication_list_id], [record_id], [medication_id], [pre_medication_dosis_per_day], [pre_medication_comment]) VALUES (3, 4, 5, CAST(200.00000 AS Decimal(18, 5)), NULL)
SET IDENTITY_INSERT [dbo].[IDACO_patient_pre_medication_list] OFF
SET IDENTITY_INSERT [dbo].[IDACO_pre_disease] ON 

INSERT [dbo].[IDACO_pre_disease] ([pre_disease_id], [pre_disease_name], [pre_disease_shortcut]) VALUES (1, N'TBD', N'TBD')
INSERT [dbo].[IDACO_pre_disease] ([pre_disease_id], [pre_disease_name], [pre_disease_shortcut]) VALUES (3, N'Appendektomie', N'append')
INSERT [dbo].[IDACO_pre_disease] ([pre_disease_id], [pre_disease_name], [pre_disease_shortcut]) VALUES (4, N'Stenting', N'stent')
INSERT [dbo].[IDACO_pre_disease] ([pre_disease_id], [pre_disease_name], [pre_disease_shortcut]) VALUES (5, N'endoskopisch retrograde Cholangio-Pankreatikografie', N'ercp')
SET IDENTITY_INSERT [dbo].[IDACO_pre_disease] OFF
SET IDENTITY_INSERT [dbo].[IDACO_pre_disease_type] ON 

INSERT [dbo].[IDACO_pre_disease_type] ([pre_disease_type_id], [pre_disease_type_name], [pre_disease_type_description]) VALUES (1, N'abdominal', N'Unterleibs-/Baucherkrankung')
INSERT [dbo].[IDACO_pre_disease_type] ([pre_disease_type_id], [pre_disease_type_name], [pre_disease_type_description]) VALUES (2, N'unknown', N'Unbekannt')
INSERT [dbo].[IDACO_pre_disease_type] ([pre_disease_type_id], [pre_disease_type_name], [pre_disease_type_description]) VALUES (3, N'cardiac', N'Herzerkrankung')
INSERT [dbo].[IDACO_pre_disease_type] ([pre_disease_type_id], [pre_disease_type_name], [pre_disease_type_description]) VALUES (5, N'intervention abdominal', N'Abdomaler Eingriff')
INSERT [dbo].[IDACO_pre_disease_type] ([pre_disease_type_id], [pre_disease_type_name], [pre_disease_type_description]) VALUES (6, N'intervention cardiac', N'Kardiologischer Eingriff')
SET IDENTITY_INSERT [dbo].[IDACO_pre_disease_type] OFF
SET IDENTITY_INSERT [dbo].[IDACO_session] ON 

INSERT [dbo].[IDACO_session] ([session_id], [session_datetime], [session_case_number], [session_type_id], [patient_id], [primary_surgeon_id], [secondary_surgeon_id], [session_comment]) VALUES (2, CAST(N'2016-01-30 12:08:24.260' AS DateTime), N'098778254564', 1, 1, 1, 10, N'dummy CHE')
INSERT [dbo].[IDACO_session] ([session_id], [session_datetime], [session_case_number], [session_type_id], [patient_id], [primary_surgeon_id], [secondary_surgeon_id], [session_comment]) VALUES (3, CAST(N'2016-11-23 10:00:00.000' AS DateTime), N'098998512345', 2, 2, 17, 20, N'dummy Sigmaresektion')
SET IDENTITY_INSERT [dbo].[IDACO_session] OFF
SET IDENTITY_INSERT [dbo].[IDACO_session_record] ON 

INSERT [dbo].[IDACO_session_record] ([record_id], [patient_id], [session_id], [hospital_admission_date], [urgency_level_id], [record_comment]) VALUES (3, 1, 2, CAST(N'2016-01-29 10:40:00.000' AS DateTime), 1, N'dummy record comment')
INSERT [dbo].[IDACO_session_record] ([record_id], [patient_id], [session_id], [hospital_admission_date], [urgency_level_id], [record_comment]) VALUES (4, 2, 3, CAST(N'2016-11-20 15:13:00.000' AS DateTime), 1, N'dummy sigma record comment')
SET IDENTITY_INSERT [dbo].[IDACO_session_record] OFF
SET IDENTITY_INSERT [dbo].[IDACO_session_team] ON 

INSERT [dbo].[IDACO_session_team] ([session_team_id], [session_id], [staff_id], [staff_is_present], [datetime_last_change]) VALUES (1, 2, 105, 1, CAST(N'2016-01-30 11:38:24.000' AS DateTime))
INSERT [dbo].[IDACO_session_team] ([session_team_id], [session_id], [staff_id], [staff_is_present], [datetime_last_change]) VALUES (3, 2, 107, 1, CAST(N'2016-01-30 11:40:24.260' AS DateTime))
INSERT [dbo].[IDACO_session_team] ([session_team_id], [session_id], [staff_id], [staff_is_present], [datetime_last_change]) VALUES (4, 2, 10, 1, CAST(N'2016-01-30 12:00:00.000' AS DateTime))
INSERT [dbo].[IDACO_session_team] ([session_team_id], [session_id], [staff_id], [staff_is_present], [datetime_last_change]) VALUES (5, 2, 1, 1, CAST(N'2016-01-30 12:05:00.000' AS DateTime))
INSERT [dbo].[IDACO_session_team] ([session_team_id], [session_id], [staff_id], [staff_is_present], [datetime_last_change]) VALUES (11, 2, 108, 1, NULL)
INSERT [dbo].[IDACO_session_team] ([session_team_id], [session_id], [staff_id], [staff_is_present], [datetime_last_change]) VALUES (12, 3, 17, 1, CAST(N'2017-05-05 13:27:00.000' AS DateTime))
INSERT [dbo].[IDACO_session_team] ([session_team_id], [session_id], [staff_id], [staff_is_present], [datetime_last_change]) VALUES (13, 3, 20, 1, CAST(N'2017-05-05 13:29:00.000' AS DateTime))
INSERT [dbo].[IDACO_session_team] ([session_team_id], [session_id], [staff_id], [staff_is_present], [datetime_last_change]) VALUES (14, 3, 14, 1, CAST(N'2017-05-05 13:30:55.000' AS DateTime))
INSERT [dbo].[IDACO_session_team] ([session_team_id], [session_id], [staff_id], [staff_is_present], [datetime_last_change]) VALUES (16, 3, 107, 1, CAST(N'2017-05-05 13:33:33.000' AS DateTime))
INSERT [dbo].[IDACO_session_team] ([session_team_id], [session_id], [staff_id], [staff_is_present], [datetime_last_change]) VALUES (17, 3, 15, 1, CAST(N'2017-05-05 13:34:11.123' AS DateTime))
INSERT [dbo].[IDACO_session_team] ([session_team_id], [session_id], [staff_id], [staff_is_present], [datetime_last_change]) VALUES (18, 3, 109, 1, CAST(N'2017-05-05 13:36:52.207' AS DateTime))
SET IDENTITY_INSERT [dbo].[IDACO_session_team] OFF
SET IDENTITY_INSERT [dbo].[IDACO_session_type] ON 

INSERT [dbo].[IDACO_session_type] ([session_type_id], [session_type_name], [session_type_description]) VALUES (1, N'Cholezystektomie', N'Entfernung der Gallenblase')
INSERT [dbo].[IDACO_session_type] ([session_type_id], [session_type_name], [session_type_description]) VALUES (2, N'Sigmaresektion', N'Operation des Endabschnittes des Dickdarms')
INSERT [dbo].[IDACO_session_type] ([session_type_id], [session_type_name], [session_type_description]) VALUES (3, N'Duodenopankreatektomie', N'Totale oder teilweise Entfernung des Pankreas (Bauchspeicheldrüse) mitsamt dem Duodenum (Zwölffingerdarm)')
SET IDENTITY_INSERT [dbo].[IDACO_session_type] OFF
SET IDENTITY_INSERT [dbo].[IDACO_setup] ON 

INSERT [dbo].[IDACO_setup] ([setup_id], [setup_name], [session_type_id]) VALUES (1, N'blau', 1)
INSERT [dbo].[IDACO_setup] ([setup_id], [setup_name], [session_type_id]) VALUES (2, N'rot', 1)
INSERT [dbo].[IDACO_setup] ([setup_id], [setup_name], [session_type_id]) VALUES (3, N'gelb', 2)
SET IDENTITY_INSERT [dbo].[IDACO_setup] OFF
SET IDENTITY_INSERT [dbo].[IDACO_setup_device] ON 

INSERT [dbo].[IDACO_setup_device] ([setup_device_id], [setup_id], [device_id]) VALUES (1, 1, 3)
INSERT [dbo].[IDACO_setup_device] ([setup_device_id], [setup_id], [device_id]) VALUES (2, 2, 3)
INSERT [dbo].[IDACO_setup_device] ([setup_device_id], [setup_id], [device_id]) VALUES (20, 3, 3)
INSERT [dbo].[IDACO_setup_device] ([setup_device_id], [setup_id], [device_id]) VALUES (21, 2, 27)
INSERT [dbo].[IDACO_setup_device] ([setup_device_id], [setup_id], [device_id]) VALUES (23, 2, 6)
INSERT [dbo].[IDACO_setup_device] ([setup_device_id], [setup_id], [device_id]) VALUES (25, 3, 26)
INSERT [dbo].[IDACO_setup_device] ([setup_device_id], [setup_id], [device_id]) VALUES (26, 2, 26)
INSERT [dbo].[IDACO_setup_device] ([setup_device_id], [setup_id], [device_id]) VALUES (27, 1, 26)
INSERT [dbo].[IDACO_setup_device] ([setup_device_id], [setup_id], [device_id]) VALUES (28, 1, 6)
INSERT [dbo].[IDACO_setup_device] ([setup_device_id], [setup_id], [device_id]) VALUES (29, 1, 31)
INSERT [dbo].[IDACO_setup_device] ([setup_device_id], [setup_id], [device_id]) VALUES (30, 1, 27)
INSERT [dbo].[IDACO_setup_device] ([setup_device_id], [setup_id], [device_id]) VALUES (31, 2, 28)
INSERT [dbo].[IDACO_setup_device] ([setup_device_id], [setup_id], [device_id]) VALUES (32, 3, 27)
INSERT [dbo].[IDACO_setup_device] ([setup_device_id], [setup_id], [device_id]) VALUES (33, 3, 6)
INSERT [dbo].[IDACO_setup_device] ([setup_device_id], [setup_id], [device_id]) VALUES (34, 3, 31)
SET IDENTITY_INSERT [dbo].[IDACO_setup_device] OFF
SET IDENTITY_INSERT [dbo].[IDACO_SI_unit] ON 

INSERT [dbo].[IDACO_SI_unit] ([SI_unit_id], [SI_unit_type_shortcut], [SI_unit_type], [SI_unit_usage]) VALUES (1, N'sec', N'second   ', N'Time estimation')
INSERT [dbo].[IDACO_SI_unit] ([SI_unit_id], [SI_unit_type_shortcut], [SI_unit_type], [SI_unit_usage]) VALUES (2, N'm', N'meter    ', N'length measurement')
INSERT [dbo].[IDACO_SI_unit] ([SI_unit_id], [SI_unit_type_shortcut], [SI_unit_type], [SI_unit_usage]) VALUES (3, N'l', N'liter    ', N'volume measurement')
INSERT [dbo].[IDACO_SI_unit] ([SI_unit_id], [SI_unit_type_shortcut], [SI_unit_type], [SI_unit_usage]) VALUES (4, N'ml', N'mililiter', N'volume measurement')
INSERT [dbo].[IDACO_SI_unit] ([SI_unit_id], [SI_unit_type_shortcut], [SI_unit_type], [SI_unit_usage]) VALUES (5, N'g/ml', N'gram per mililiter', N'density')
INSERT [dbo].[IDACO_SI_unit] ([SI_unit_id], [SI_unit_type_shortcut], [SI_unit_type], [SI_unit_usage]) VALUES (6, N'-', N'unitless', N'unitless ')
INSERT [dbo].[IDACO_SI_unit] ([SI_unit_id], [SI_unit_type_shortcut], [SI_unit_type], [SI_unit_usage]) VALUES (7, N'mmol/l', N'milimol per liter', N'amount of substance per volume')
INSERT [dbo].[IDACO_SI_unit] ([SI_unit_id], [SI_unit_type_shortcut], [SI_unit_type], [SI_unit_usage]) VALUES (8, N'mg/dl', N'miligram per deciliter', N'amount of substance per volume')
INSERT [dbo].[IDACO_SI_unit] ([SI_unit_id], [SI_unit_type_shortcut], [SI_unit_type], [SI_unit_usage]) VALUES (9, N'U/l', N'unknown', N'unknown')
INSERT [dbo].[IDACO_SI_unit] ([SI_unit_id], [SI_unit_type_shortcut], [SI_unit_type], [SI_unit_usage]) VALUES (10, N'g/dl', N'gram per deciliter', N'amount of substance per volume')
INSERT [dbo].[IDACO_SI_unit] ([SI_unit_id], [SI_unit_type_shortcut], [SI_unit_type], [SI_unit_usage]) VALUES (11, N'%', N'percent', N'percentage composition')
INSERT [dbo].[IDACO_SI_unit] ([SI_unit_id], [SI_unit_type_shortcut], [SI_unit_type], [SI_unit_usage]) VALUES (12, N'g/dl', N'gram per deciliter', N'amount of substance per volume')
INSERT [dbo].[IDACO_SI_unit] ([SI_unit_id], [SI_unit_type_shortcut], [SI_unit_type], [SI_unit_usage]) VALUES (13, N'cm', N'centimeter', N'length measurement')
INSERT [dbo].[IDACO_SI_unit] ([SI_unit_id], [SI_unit_type_shortcut], [SI_unit_type], [SI_unit_usage]) VALUES (14, N'Ø cm', N'diameter in centimeter', N'diameter measurement')
INSERT [dbo].[IDACO_SI_unit] ([SI_unit_id], [SI_unit_type_shortcut], [SI_unit_type], [SI_unit_usage]) VALUES (15, N'°', N'degree', N'angle measurement')
INSERT [dbo].[IDACO_SI_unit] ([SI_unit_id], [SI_unit_type_shortcut], [SI_unit_type], [SI_unit_usage]) VALUES (16, N'kg', N'kiologram', N'weight measurement')
INSERT [dbo].[IDACO_SI_unit] ([SI_unit_id], [SI_unit_type_shortcut], [SI_unit_type], [SI_unit_usage]) VALUES (17, N'mm', N'milimeter', N'length measurement')
INSERT [dbo].[IDACO_SI_unit] ([SI_unit_id], [SI_unit_type_shortcut], [SI_unit_type], [SI_unit_usage]) VALUES (18, N'mg/l', N'miligram per liter', N'density')
INSERT [dbo].[IDACO_SI_unit] ([SI_unit_id], [SI_unit_type_shortcut], [SI_unit_type], [SI_unit_usage]) VALUES (19, N'mg', N'miligram', N'weight measurement')
SET IDENTITY_INSERT [dbo].[IDACO_SI_unit] OFF
SET IDENTITY_INSERT [dbo].[IDACO_staff] ON 

INSERT [dbo].[IDACO_staff] ([staff_id], [staff_role_id], [staff_title], [staff_name], [staff_surname], [staff_experience]) VALUES (1, 1, N'Prof. Dr. med.      ', N'Hubertus', N'Feußner', 3)
INSERT [dbo].[IDACO_staff] ([staff_id], [staff_role_id], [staff_title], [staff_name], [staff_surname], [staff_experience]) VALUES (3, 1, N'Dr.                 ', N'Tara', N'Müller', 3)
INSERT [dbo].[IDACO_staff] ([staff_id], [staff_role_id], [staff_title], [staff_name], [staff_surname], [staff_experience]) VALUES (4, 1, N'Dott.               ', N'Chiara', N'Tosolini', 3)
INSERT [dbo].[IDACO_staff] ([staff_id], [staff_role_id], [staff_title], [staff_name], [staff_surname], [staff_experience]) VALUES (7, 1, N'Dr.                 ', N'Maria Rebekka', N'Schirren', 3)
INSERT [dbo].[IDACO_staff] ([staff_id], [staff_role_id], [staff_title], [staff_name], [staff_surname], [staff_experience]) VALUES (8, 1, N'Dr.                 ', N'Edouard', N'Matevossian', 3)
INSERT [dbo].[IDACO_staff] ([staff_id], [staff_role_id], [staff_title], [staff_name], [staff_surname], [staff_experience]) VALUES (10, 1, N'Dr.                 ', N'Michael', N'Kranzfelder', 3)
INSERT [dbo].[IDACO_staff] ([staff_id], [staff_role_id], [staff_title], [staff_name], [staff_surname], [staff_experience]) VALUES (11, 1, N'Dr.                 ', N'Ihsan Ekin', N'Demir', 3)
INSERT [dbo].[IDACO_staff] ([staff_id], [staff_role_id], [staff_title], [staff_name], [staff_surname], [staff_experience]) VALUES (12, 1, N'Dr. Dr.             ', N'Bo', N'Kong', 3)
INSERT [dbo].[IDACO_staff] ([staff_id], [staff_role_id], [staff_title], [staff_name], [staff_surname], [staff_experience]) VALUES (13, 1, N'Dr. med.            ', N'Güralp', N'Ceyhan', 3)
INSERT [dbo].[IDACO_staff] ([staff_id], [staff_role_id], [staff_title], [staff_name], [staff_surname], [staff_experience]) VALUES (14, 4, NULL, N'Barbara', N'Detter', 3)
INSERT [dbo].[IDACO_staff] ([staff_id], [staff_role_id], [staff_title], [staff_name], [staff_surname], [staff_experience]) VALUES (15, 6, N'Prof. Dr. med. apl. ', NULL, N'Blobner', 3)
INSERT [dbo].[IDACO_staff] ([staff_id], [staff_role_id], [staff_title], [staff_name], [staff_surname], [staff_experience]) VALUES (17, 1, N'Dr.                 ', N'Dirk', N'Wilhelm', 3)
INSERT [dbo].[IDACO_staff] ([staff_id], [staff_role_id], [staff_title], [staff_name], [staff_surname], [staff_experience]) VALUES (19, 1, N'Prof. Dr. med.      ', N'Helmut', N'Friess', 3)
INSERT [dbo].[IDACO_staff] ([staff_id], [staff_role_id], [staff_title], [staff_name], [staff_surname], [staff_experience]) VALUES (20, 1, N'Dr. med.            ', N'Elke', N'Tieftrunk', 1)
INSERT [dbo].[IDACO_staff] ([staff_id], [staff_role_id], [staff_title], [staff_name], [staff_surname], [staff_experience]) VALUES (21, 1, N'                    ', N'Mine', N'Sargut', 1)
INSERT [dbo].[IDACO_staff] ([staff_id], [staff_role_id], [staff_title], [staff_name], [staff_surname], [staff_experience]) VALUES (22, 1, N'                    ', N'Thomas', N'Vogel', 1)
INSERT [dbo].[IDACO_staff] ([staff_id], [staff_role_id], [staff_title], [staff_name], [staff_surname], [staff_experience]) VALUES (99, 1, N'                    ', N'UNBEKANNT', N'UNBEKANNT', 1)
INSERT [dbo].[IDACO_staff] ([staff_id], [staff_role_id], [staff_title], [staff_name], [staff_surname], [staff_experience]) VALUES (100, 1, N'Dr.                 ', N'Jan', N'Saeckl', 3)
INSERT [dbo].[IDACO_staff] ([staff_id], [staff_role_id], [staff_title], [staff_name], [staff_surname], [staff_experience]) VALUES (101, 1, N'Dr.                 ', N'Ralf', N'Gertler', 3)
INSERT [dbo].[IDACO_staff] ([staff_id], [staff_role_id], [staff_title], [staff_name], [staff_surname], [staff_experience]) VALUES (102, 1, N'Dr.                 ', N'Daniel', N'Hartmann', 2)
INSERT [dbo].[IDACO_staff] ([staff_id], [staff_role_id], [staff_title], [staff_name], [staff_surname], [staff_experience]) VALUES (103, 1, N'Dr.                 ', N'Stephan', N'Schorn', 1)
INSERT [dbo].[IDACO_staff] ([staff_id], [staff_role_id], [staff_title], [staff_name], [staff_surname], [staff_experience]) VALUES (104, 1, N'Dr.                 ', N'Lenika', N'Calavrezos', 1)
INSERT [dbo].[IDACO_staff] ([staff_id], [staff_role_id], [staff_title], [staff_name], [staff_surname], [staff_experience]) VALUES (105, 4, NULL, N'Erika', N'Steril', 3)
INSERT [dbo].[IDACO_staff] ([staff_id], [staff_role_id], [staff_title], [staff_name], [staff_surname], [staff_experience]) VALUES (107, 5, NULL, N'Monika', N'Springer', 3)
INSERT [dbo].[IDACO_staff] ([staff_id], [staff_role_id], [staff_title], [staff_name], [staff_surname], [staff_experience]) VALUES (108, 6, N'Dr.                 ', N'Hans', N'Hasenfuss', 3)
INSERT [dbo].[IDACO_staff] ([staff_id], [staff_role_id], [staff_title], [staff_name], [staff_surname], [staff_experience]) VALUES (109, 7, NULL, N'PJ', N'Student', 1)
SET IDENTITY_INSERT [dbo].[IDACO_staff] OFF
SET IDENTITY_INSERT [dbo].[IDACO_staff_role] ON 

INSERT [dbo].[IDACO_staff_role] ([staff_role_id], [staff_role_name], [staff_role_description]) VALUES (1, N'Chirurg', N'Chirurg')
INSERT [dbo].[IDACO_staff_role] ([staff_role_id], [staff_role_name], [staff_role_description]) VALUES (3, N'Assistent Arzt', N'Assistent Arzt')
INSERT [dbo].[IDACO_staff_role] ([staff_role_id], [staff_role_name], [staff_role_description]) VALUES (4, N'OP Schwester', N'OP Schwester')
INSERT [dbo].[IDACO_staff_role] ([staff_role_id], [staff_role_name], [staff_role_description]) VALUES (5, N'Springer', N'Springer')
INSERT [dbo].[IDACO_staff_role] ([staff_role_id], [staff_role_name], [staff_role_description]) VALUES (6, N'Anästhesist', N'Anästhesist')
INSERT [dbo].[IDACO_staff_role] ([staff_role_id], [staff_role_name], [staff_role_description]) VALUES (7, N'PJ', N'Student im praktischen Jahr')
SET IDENTITY_INSERT [dbo].[IDACO_staff_role] OFF
SET IDENTITY_INSERT [dbo].[IDACO_staff_setup] ON 

INSERT [dbo].[IDACO_staff_setup] ([staff_setup_id], [staff_id], [setup_id], [setup_active]) VALUES (1, 1, 1, 0)
INSERT [dbo].[IDACO_staff_setup] ([staff_setup_id], [staff_id], [setup_id], [setup_active]) VALUES (2, 17, 3, 1)
INSERT [dbo].[IDACO_staff_setup] ([staff_setup_id], [staff_id], [setup_id], [setup_active]) VALUES (3, 1, 2, 1)
INSERT [dbo].[IDACO_staff_setup] ([staff_setup_id], [staff_id], [setup_id], [setup_active]) VALUES (4, 1, 3, 1)
SET IDENTITY_INSERT [dbo].[IDACO_staff_setup] OFF
SET IDENTITY_INSERT [dbo].[IDACO_urgency_level] ON 

INSERT [dbo].[IDACO_urgency_level] ([urgency_level_id], [urgency_level_name], [urgency_level_description]) VALUES (1, N'elektiv', N'keine unmittelbare Dringlichkeit')
INSERT [dbo].[IDACO_urgency_level] ([urgency_level_id], [urgency_level_name], [urgency_level_description]) VALUES (2, N'akut', N'sehr dringend')
SET IDENTITY_INSERT [dbo].[IDACO_urgency_level] OFF
ALTER TABLE [dbo].[IDACO_device]  WITH CHECK ADD  CONSTRAINT [FK_IDACO_device_IDACO_device_type] FOREIGN KEY([device_type_id])
REFERENCES [dbo].[IDACO_device_type] ([device_type_id])
GO
ALTER TABLE [dbo].[IDACO_device] CHECK CONSTRAINT [FK_IDACO_device_IDACO_device_type]
GO
ALTER TABLE [dbo].[IDACO_device_parameter_list]  WITH CHECK ADD  CONSTRAINT [FK_IDACO_device_parameter_list_IDACO_device] FOREIGN KEY([device_list_id])
REFERENCES [dbo].[IDACO_device] ([device_list_id])
GO
ALTER TABLE [dbo].[IDACO_device_parameter_list] CHECK CONSTRAINT [FK_IDACO_device_parameter_list_IDACO_device]
GO
ALTER TABLE [dbo].[IDACO_device_status]  WITH CHECK ADD  CONSTRAINT [FK_IDACO_device_status_IDACO_device] FOREIGN KEY([device_list_id])
REFERENCES [dbo].[IDACO_device] ([device_list_id])
GO
ALTER TABLE [dbo].[IDACO_device_status] CHECK CONSTRAINT [FK_IDACO_device_status_IDACO_device]
GO
ALTER TABLE [dbo].[IDACO_device_status]  WITH CHECK ADD  CONSTRAINT [FK_IDACO_device_status_IDACO_session] FOREIGN KEY([session_id])
REFERENCES [dbo].[IDACO_session] ([session_id])
GO
ALTER TABLE [dbo].[IDACO_device_status] CHECK CONSTRAINT [FK_IDACO_device_status_IDACO_session]
GO
ALTER TABLE [dbo].[IDACO_device_status_setup]  WITH CHECK ADD  CONSTRAINT [FK_IDACO_device_status_setup_IDACO_device] FOREIGN KEY([device_list_id])
REFERENCES [dbo].[IDACO_device] ([device_list_id])
GO
ALTER TABLE [dbo].[IDACO_device_status_setup] CHECK CONSTRAINT [FK_IDACO_device_status_setup_IDACO_device]
GO
ALTER TABLE [dbo].[IDACO_device_status_setup]  WITH CHECK ADD  CONSTRAINT [FK_IDACO_device_status_setup_IDACO_setup] FOREIGN KEY([setup_id])
REFERENCES [dbo].[IDACO_setup] ([setup_id])
GO
ALTER TABLE [dbo].[IDACO_device_status_setup] CHECK CONSTRAINT [FK_IDACO_device_status_setup_IDACO_setup]
GO
ALTER TABLE [dbo].[IDACO_device_status_target]  WITH CHECK ADD  CONSTRAINT [FK_IDACO_device_status_target_IDACO_device] FOREIGN KEY([device_list_id])
REFERENCES [dbo].[IDACO_device] ([device_list_id])
GO
ALTER TABLE [dbo].[IDACO_device_status_target] CHECK CONSTRAINT [FK_IDACO_device_status_target_IDACO_device]
GO
ALTER TABLE [dbo].[IDACO_device_status_target]  WITH CHECK ADD  CONSTRAINT [FK_IDACO_device_status_target_IDACO_session] FOREIGN KEY([session_id])
REFERENCES [dbo].[IDACO_session] ([session_id])
GO
ALTER TABLE [dbo].[IDACO_device_status_target] CHECK CONSTRAINT [FK_IDACO_device_status_target_IDACO_session]
GO
ALTER TABLE [dbo].[IDACO_laboratory_data]  WITH CHECK ADD  CONSTRAINT [FK_IDACO_laboratory_data_IDACO_SI_unit] FOREIGN KEY([SI_unit_id])
REFERENCES [dbo].[IDACO_SI_unit] ([SI_unit_id])
GO
ALTER TABLE [dbo].[IDACO_laboratory_data] CHECK CONSTRAINT [FK_IDACO_laboratory_data_IDACO_SI_unit]
GO
ALTER TABLE [dbo].[IDACO_laboratory_data_list]  WITH CHECK ADD  CONSTRAINT [FK_IDACO_laboratory_data_list_IDACO_laboratory_data] FOREIGN KEY([laboratory_data_id])
REFERENCES [dbo].[IDACO_laboratory_data] ([laboratory_data_id])
GO
ALTER TABLE [dbo].[IDACO_laboratory_data_list] CHECK CONSTRAINT [FK_IDACO_laboratory_data_list_IDACO_laboratory_data]
GO
ALTER TABLE [dbo].[IDACO_laboratory_data_list]  WITH CHECK ADD  CONSTRAINT [FK_IDACO_laboratory_data_list_IDACO_session_record] FOREIGN KEY([record_id])
REFERENCES [dbo].[IDACO_session_record] ([record_id])
GO
ALTER TABLE [dbo].[IDACO_laboratory_data_list] CHECK CONSTRAINT [FK_IDACO_laboratory_data_list_IDACO_session_record]
GO
ALTER TABLE [dbo].[IDACO_medications]  WITH CHECK ADD  CONSTRAINT [FK_IDACO_medications_IDACO_SI_unit] FOREIGN KEY([SI_unit_id])
REFERENCES [dbo].[IDACO_SI_unit] ([SI_unit_id])
GO
ALTER TABLE [dbo].[IDACO_medications] CHECK CONSTRAINT [FK_IDACO_medications_IDACO_SI_unit]
GO
ALTER TABLE [dbo].[IDACO_patient]  WITH CHECK ADD  CONSTRAINT [FK_IDACO_patient_IDACO_gender_value] FOREIGN KEY([patient_gender])
REFERENCES [dbo].[IDACO_gender_value] ([patient_gender])
GO
ALTER TABLE [dbo].[IDACO_patient] CHECK CONSTRAINT [FK_IDACO_patient_IDACO_gender_value]
GO
ALTER TABLE [dbo].[IDACO_patient_pre_disease_list]  WITH CHECK ADD  CONSTRAINT [FK_IDACO_patient_pre_disease_list_IDACO_pre_disease] FOREIGN KEY([pre_disease_id])
REFERENCES [dbo].[IDACO_pre_disease] ([pre_disease_id])
GO
ALTER TABLE [dbo].[IDACO_patient_pre_disease_list] CHECK CONSTRAINT [FK_IDACO_patient_pre_disease_list_IDACO_pre_disease]
GO
ALTER TABLE [dbo].[IDACO_patient_pre_disease_list]  WITH CHECK ADD  CONSTRAINT [FK_IDACO_patient_pre_disease_list_IDACO_pre_disease_type] FOREIGN KEY([pre_disease_type_id])
REFERENCES [dbo].[IDACO_pre_disease_type] ([pre_disease_type_id])
GO
ALTER TABLE [dbo].[IDACO_patient_pre_disease_list] CHECK CONSTRAINT [FK_IDACO_patient_pre_disease_list_IDACO_pre_disease_type]
GO
ALTER TABLE [dbo].[IDACO_patient_pre_disease_list]  WITH CHECK ADD  CONSTRAINT [FK_IDACO_patient_pre_disease_list_IDACO_session_record] FOREIGN KEY([record_id])
REFERENCES [dbo].[IDACO_session_record] ([record_id])
GO
ALTER TABLE [dbo].[IDACO_patient_pre_disease_list] CHECK CONSTRAINT [FK_IDACO_patient_pre_disease_list_IDACO_session_record]
GO
ALTER TABLE [dbo].[IDACO_patient_pre_medication_list]  WITH CHECK ADD  CONSTRAINT [FK_IDACO_patient_pre_medication_list_IDACO_medications] FOREIGN KEY([medication_id])
REFERENCES [dbo].[IDACO_medications] ([medication_id])
GO
ALTER TABLE [dbo].[IDACO_patient_pre_medication_list] CHECK CONSTRAINT [FK_IDACO_patient_pre_medication_list_IDACO_medications]
GO
ALTER TABLE [dbo].[IDACO_patient_pre_medication_list]  WITH CHECK ADD  CONSTRAINT [FK_IDACO_patient_pre_medication_list_IDACO_session_record] FOREIGN KEY([record_id])
REFERENCES [dbo].[IDACO_session_record] ([record_id])
GO
ALTER TABLE [dbo].[IDACO_patient_pre_medication_list] CHECK CONSTRAINT [FK_IDACO_patient_pre_medication_list_IDACO_session_record]
GO
ALTER TABLE [dbo].[IDACO_session]  WITH CHECK ADD  CONSTRAINT [FK_IDACO_session_IDACO_session_type] FOREIGN KEY([session_type_id])
REFERENCES [dbo].[IDACO_session_type] ([session_type_id])
GO
ALTER TABLE [dbo].[IDACO_session] CHECK CONSTRAINT [FK_IDACO_session_IDACO_session_type]
GO
ALTER TABLE [dbo].[IDACO_session]  WITH CHECK ADD  CONSTRAINT [FK_IDACO_session_IDACO_staff] FOREIGN KEY([primary_surgeon_id])
REFERENCES [dbo].[IDACO_staff] ([staff_id])
GO
ALTER TABLE [dbo].[IDACO_session] CHECK CONSTRAINT [FK_IDACO_session_IDACO_staff]
GO
ALTER TABLE [dbo].[IDACO_session]  WITH CHECK ADD  CONSTRAINT [FK_IDACO_session_IDACO_staff1] FOREIGN KEY([secondary_surgeon_id])
REFERENCES [dbo].[IDACO_staff] ([staff_id])
GO
ALTER TABLE [dbo].[IDACO_session] CHECK CONSTRAINT [FK_IDACO_session_IDACO_staff1]
GO
ALTER TABLE [dbo].[IDACO_session_record]  WITH CHECK ADD  CONSTRAINT [FK_IDACO_session_record_IDACO_patient] FOREIGN KEY([patient_id])
REFERENCES [dbo].[IDACO_patient] ([patient_id])
GO
ALTER TABLE [dbo].[IDACO_session_record] CHECK CONSTRAINT [FK_IDACO_session_record_IDACO_patient]
GO
ALTER TABLE [dbo].[IDACO_session_record]  WITH CHECK ADD  CONSTRAINT [FK_IDACO_session_record_IDACO_session1] FOREIGN KEY([session_id])
REFERENCES [dbo].[IDACO_session] ([session_id])
GO
ALTER TABLE [dbo].[IDACO_session_record] CHECK CONSTRAINT [FK_IDACO_session_record_IDACO_session1]
GO
ALTER TABLE [dbo].[IDACO_session_record]  WITH CHECK ADD  CONSTRAINT [FK_IDACO_session_record_IDACO_urgency_level] FOREIGN KEY([urgency_level_id])
REFERENCES [dbo].[IDACO_urgency_level] ([urgency_level_id])
GO
ALTER TABLE [dbo].[IDACO_session_record] CHECK CONSTRAINT [FK_IDACO_session_record_IDACO_urgency_level]
GO
ALTER TABLE [dbo].[IDACO_session_team]  WITH CHECK ADD  CONSTRAINT [FK_IDACO_session_team_IDACO_session] FOREIGN KEY([session_id])
REFERENCES [dbo].[IDACO_session] ([session_id])
GO
ALTER TABLE [dbo].[IDACO_session_team] CHECK CONSTRAINT [FK_IDACO_session_team_IDACO_session]
GO
ALTER TABLE [dbo].[IDACO_session_team]  WITH CHECK ADD  CONSTRAINT [FK_IDACO_session_team_IDACO_staff] FOREIGN KEY([staff_id])
REFERENCES [dbo].[IDACO_staff] ([staff_id])
GO
ALTER TABLE [dbo].[IDACO_session_team] CHECK CONSTRAINT [FK_IDACO_session_team_IDACO_staff]
GO
ALTER TABLE [dbo].[IDACO_setup_device]  WITH CHECK ADD  CONSTRAINT [FK_IDACO_setup_device_IDACO_device] FOREIGN KEY([device_id])
REFERENCES [dbo].[IDACO_device] ([device_list_id])
GO
ALTER TABLE [dbo].[IDACO_setup_device] CHECK CONSTRAINT [FK_IDACO_setup_device_IDACO_device]
GO
ALTER TABLE [dbo].[IDACO_setup_device]  WITH CHECK ADD  CONSTRAINT [FK_IDACO_setup_device_IDACO_setup] FOREIGN KEY([setup_id])
REFERENCES [dbo].[IDACO_setup] ([setup_id])
GO
ALTER TABLE [dbo].[IDACO_setup_device] CHECK CONSTRAINT [FK_IDACO_setup_device_IDACO_setup]
GO
ALTER TABLE [dbo].[IDACO_staff]  WITH CHECK ADD  CONSTRAINT [FK_IDACO_staff_IDACO_staff_role] FOREIGN KEY([staff_role_id])
REFERENCES [dbo].[IDACO_staff_role] ([staff_role_id])
GO
ALTER TABLE [dbo].[IDACO_staff] CHECK CONSTRAINT [FK_IDACO_staff_IDACO_staff_role]
GO
ALTER TABLE [dbo].[IDACO_staff_setup]  WITH CHECK ADD  CONSTRAINT [FK_IDACO_staff_setup_IDACO_setup] FOREIGN KEY([setup_id])
REFERENCES [dbo].[IDACO_setup] ([setup_id])
GO
ALTER TABLE [dbo].[IDACO_staff_setup] CHECK CONSTRAINT [FK_IDACO_staff_setup_IDACO_setup]
GO
ALTER TABLE [dbo].[IDACO_staff_setup]  WITH CHECK ADD  CONSTRAINT [FK_IDACO_staff_setup_IDACO_staff] FOREIGN KEY([staff_id])
REFERENCES [dbo].[IDACO_staff] ([staff_id])
GO
ALTER TABLE [dbo].[IDACO_staff_setup] CHECK CONSTRAINT [FK_IDACO_staff_setup_IDACO_staff]
GO
/****** Object:  StoredProcedure [dbo].[_old_sp_Get_All_Devices]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[_old_sp_Get_All_Devices]
	-- Add the parameters for the stored procedure here
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	
	select ID as DevID, StringParam as DevName  from idaco_devices where Name = 'Device' 


END



GO
/****** Object:  StoredProcedure [dbo].[_old_sp_Get_Device_NumParam_by_ID]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[_old_sp_Get_Device_NumParam_by_ID] 
	-- Add the parameters for the stored procedure here
	@DevID INT,
	@ParamID INT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    select NumParam from idaco_devices where ParentID = @DevID and ID=@ParamID

END



GO
/****** Object:  StoredProcedure [dbo].[_old_sp_Get_Device_NumParam_by_Name]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[_old_sp_Get_Device_NumParam_by_Name] 
	-- Add the parameters for the stored procedure here
	@DevID INT,
	@ParamName NVARCHAR(MAX)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    select NumParam from idaco_devices where ParentID = @DevID and Name=@ParamName

END



GO
/****** Object:  StoredProcedure [dbo].[_old_sp_Get_Device_StringParam_by_ID]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[_old_sp_Get_Device_StringParam_by_ID] 
	-- Add the parameters for the stored procedure here
	@DevID INT,
	@ParamID INT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    select StringParam from idaco_devices where ParentID = @DevID and ID=@ParamId

END



GO
/****** Object:  StoredProcedure [dbo].[_old_sp_Get_Device_StringParam_by_Name]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[_old_sp_Get_Device_StringParam_by_Name] 
	-- Add the parameters for the stored procedure here
	@DevID INT,
	@ParamName NVARCHAR(MAX)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    select StringParam from idaco_devices where ParentID = @DevID and Name=@ParamName

END



GO
/****** Object:  StoredProcedure [dbo].[_old_sp_Update_Device_NumParam_by_ID]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[_old_sp_Update_Device_NumParam_by_ID]
	-- Add the parameters for the stored procedure here
	@DevID INT,
	@ParamID INT,
	@ParamValue REAL
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	update idaco_devices
	set NumParam = @ParamValue 
	where ID = @ParamID and ParentID = @DevID
END



GO
/****** Object:  StoredProcedure [dbo].[_old_sp_Update_Device_NumParam_by_Name]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[_old_sp_Update_Device_NumParam_by_Name] 
	-- Add the parameters for the stored procedure here
	@DevID INT,
	@ParamName NVARCHAR(MAX),
	@ParamValue REAL
	
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

update idaco_devices
set NumParam = @ParamValue 
where Name = @ParamName and ParentID = @DevID


END



GO
/****** Object:  StoredProcedure [dbo].[_old_sp_Update_Device_StringParam_by_ID]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[_old_sp_Update_Device_StringParam_by_ID]
	-- Add the parameters for the stored procedure here
	@DevID INT,
	@ParamID INT,
	@ParamValue NVARCHAR(MAX)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	update idaco_devices
	set StringParam = @ParamValue  
	where ID = @ParamID and ParentID = @DevID

END



GO
/****** Object:  StoredProcedure [dbo].[_old_sp_Update_Device_StringParam_by_Name]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[_old_sp_Update_Device_StringParam_by_Name]
	-- Add the parameters for the stored procedure here
	@DevID INT,
	@ParamName NVARCHAR(MAX),
	@ParamValue NVARCHAR(MAX)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	update idaco_devices
	set StringParam = @ParamValue 
	where Name = @ParamName and ParentID = @DevID


END



GO
/****** Object:  StoredProcedure [dbo].[Procedure]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[Procedure]
	@param1 int = 0,
	@param2 int
AS
	SELECT @param1, @param2
RETURN 0

GO
/****** Object:  StoredProcedure [dbo].[sp_Get_All_Devices]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[sp_Get_All_Devices]
	-- Add the parameters for the stored procedure here
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	
	select ID as DevID, StringParam as DevName  from idaco_devices where Name = 'Device' 


END


GO
/****** Object:  StoredProcedure [dbo].[sp_Get_Current_Session]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[sp_Get_Current_Session]
	
AS
	
	SELECT CAST ( (SELECT  configuration_value FROM dbo.IDACO_configuration WHERE configuration_key = 'IDACO_CURRENT_SESSION') AS INT ) AS Session_id
RETURN 0

GO
/****** Object:  StoredProcedure [dbo].[sp_Get_Device_NumParam_by_ID]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[sp_Get_Device_NumParam_by_ID] 
	-- Add the parameters for the stored procedure here
	@DevID INT,
	@ParamID INT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    select NumParam from idaco_devices where ParentID = @DevID and ID=@ParamID

END


GO
/****** Object:  StoredProcedure [dbo].[sp_Get_Device_NumParam_by_Name]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[sp_Get_Device_NumParam_by_Name] 
	-- Add the parameters for the stored procedure here
	@DevID INT,
	@ParamName NVARCHAR(MAX)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    select NumParam from idaco_devices where ParentID = @DevID and Name=@ParamName

END


GO
/****** Object:  StoredProcedure [dbo].[sp_Get_Device_StringParam_by_ID]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[sp_Get_Device_StringParam_by_ID] 
	-- Add the parameters for the stored procedure here
	@DevID INT,
	@ParamID INT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    select StringParam from idaco_devices where ParentID = @DevID and ID=@ParamId

END


GO
/****** Object:  StoredProcedure [dbo].[sp_Get_Device_StringParam_by_Name]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[sp_Get_Device_StringParam_by_Name] 
	-- Add the parameters for the stored procedure here
	@DevID INT,
	@ParamName NVARCHAR(MAX)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    select StringParam from idaco_devices where ParentID = @DevID and Name=@ParamName

END


GO
/****** Object:  StoredProcedure [dbo].[sp_Get_DeviceID_by_Name]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[sp_Get_DeviceID_by_Name]
	-- Add the parameters for the stored procedure here
	@DeviceName NVARCHAR(MAX)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    select ID from idaco_devices 
	where Name = 'Device' 
	AND StringParam = @DeviceName
END


GO
/****** Object:  StoredProcedure [dbo].[sp_Get_DeviceID_by_Name_Output]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[sp_Get_DeviceID_by_Name_Output]
	-- Add the parameters for the stored procedure here
	@DeviceName NVARCHAR(MAX),
	@DeviceID INT OUTPUT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	
	set @DeviceID = (select ID from idaco_devices 
	where Name = 'Device' 
	AND StringParam = @DeviceName)

END


GO
/****** Object:  StoredProcedure [dbo].[sp_Get_DeviceParams_by_ID]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[sp_Get_DeviceParams_by_ID]
	-- Add the parameters for the stored procedure here
	@DevID INT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	select ID as ParamID,Name,NumParam,StringParam from idaco_devices where ParentID = @DevID
END


GO
/****** Object:  StoredProcedure [dbo].[sp_Get_DeviceParams_by_Name]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[sp_Get_DeviceParams_by_Name]
	-- Add the parameters for the stored procedure here
	@DevName NVARCHAR(MAX)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	
	select ID as ParamID,Name,NumParam,StringParam from idaco_devices where ParentID =
(select ID from idaco_devices where Name = 'Device' AND StringParam = @DevName )


END


GO
/****** Object:  StoredProcedure [dbo].[sp_Get_IDACO_Device_Status]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[sp_Get_IDACO_Device_Status]
		@device_list_id INT,
		@device_status_param_key INT,
	    

		@session_id INT = NULL
AS
	

	SELECT device_status_param_value FROM dbo.IDACO_device_status WHERE device_list_id = @device_list_id AND device_status_param_key = @device_status_param_key

RETURN 0

GO
/****** Object:  StoredProcedure [dbo].[sp_Get_IDACO_Device_Status_ByName]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[sp_Get_IDACO_Device_Status_ByName]
		@device_name NVARCHAR(MAX),
		@device_status_param_name NVARCHAR(MAX),
	    

		@session_id INT = NULL
AS
	

	DECLARE @device_id INT
	DECLARE @device_param_id INT


	SET @device_id = (SELECT TOP 1 device_list_id FROM dbo.IDACO_device WHERE device_name = @device_name)

	SET @device_param_id = (SELECT TOP 1 device_parameter_id FROM dbo.IDACO_device_parameter_list WHERE device_list_id = @device_id AND device_parameter_name = @device_status_param_name )


	IF (@device_id IS NOT NULL AND @device_param_id IS NOT NULL) 
	BEGIN
	EXEC dbo.sp_Get_IDACO_Device_Status
			@device_list_id = @device_id, -- int
		    @device_status_param_key = @device_param_id, -- int
		    @session_id = @session_id -- int
		
	RETURN 0
	END
	ELSE
	BEGIN
		PRINT N'Query Failed ' 
				
	RETURN -1
	END

GO
/****** Object:  StoredProcedure [dbo].[sp_Get_IDACO_Device_Status_Target]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[sp_Get_IDACO_Device_Status_Target]
		@device_list_id INT,
		@device_status_param_key INT,
	    

		@session_id INT = NULL
AS
	

	SELECT device_status_param_value FROM dbo.IDACO_device_status_target WHERE device_list_id = @device_list_id AND device_status_param_key = @device_status_param_key

RETURN 0

GO
/****** Object:  StoredProcedure [dbo].[sp_Get_IDACO_Device_Status_Target_ByName]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[sp_Get_IDACO_Device_Status_Target_ByName]
		@device_name NVARCHAR(MAX),
		@device_status_param_name NVARCHAR(MAX),
	    

		@session_id INT = NULL
AS
	

	DECLARE @device_id INT
	DECLARE @device_param_id INT


	SET @device_id = (SELECT TOP 1 device_list_id FROM dbo.IDACO_device WHERE device_name = @device_name)

	SET @device_param_id = (SELECT TOP 1 device_parameter_id FROM dbo.IDACO_device_parameter_list WHERE device_list_id = @device_id AND device_parameter_name = @device_status_param_name )


	IF (@device_id IS NOT NULL AND @device_param_id IS NOT NULL) 
	BEGIN
	EXEC dbo.sp_Get_IDACO_Device_Status_Target 
			@device_list_id = @device_id, -- int
		    @device_status_param_key = @device_param_id, -- int
		    @session_id = @session_id -- int
		
	RETURN 0
	END
	ELSE
	BEGIN
		PRINT N'Query Failed ' 
				
	RETURN -1
	END

GO
/****** Object:  StoredProcedure [dbo].[sp_Get_Primary_Surgeon]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[sp_Get_Primary_Surgeon] 
	-- Add the parameters for the stored procedure here
	@session_id INT

AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	return(Select primary_surgeon_id
	from IDACO_session where session_id = @session_id)

	

END


GO
/****** Object:  StoredProcedure [dbo].[sp_Get_Primary_Surgeon_Presence_and_Name]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[sp_Get_Primary_Surgeon_Presence_and_Name] 
	-- Add the parameters for the stored procedure here
	@sessionID INT,
	@surgeonName varchar(20) OUTPUT,
	@surgeonSurname varchar(20) OUTPUT,
	@surgeonPresence bit OUTPUT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    declare @primeSurg INT

	exec @primeSurg = sp_Get_Primary_Surgeon @sessionID

	set @surgeonSurname = ( select staff_surname from IDACO_staff where staff_id = @primeSurg )
	set @surgeonName = ( select staff_name from IDACO_staff where staff_id = @primeSurg )
	set @surgeonPresence = (select staff_is_present from IDACO_session_team where session_id= @sessionID and staff_id = @primeSurg)

	return 0

END


GO
/****** Object:  StoredProcedure [dbo].[sp_Get_Session_Devices_by_Staff]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[sp_Get_Session_Devices_by_Staff]
	-- Add the parameters for the stored procedure here
	@surgeon int,
	@sessionType int
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    IF OBJECT_ID('tempdb..#tmpSetupId') IS NOT NULL DROP TABLE #tmpSetupId
Create Table #tmpSetupId
(
	setup_id int NOT NULL
)

INSERT into #tmpSetupId (setup_id) (
select setup_id from IDACO_staff_setup where staff_id =@surgeon and setup_id in (select setup_id from IDACO_setup where session_type_id = @sessionType) and setup_active =1
)


select * from IDACO_setup_device where setup_id in (select * from #tmpSetupId) 

END





GO
/****** Object:  StoredProcedure [dbo].[sp_Update_Current_Session]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[sp_Update_Current_Session]

@session_id INT

AS
	UPDATE dbo.IDACO_configuration SET configuration_value = CAST(@session_id AS NVARCHAR(MAX))
	WHERE configuration_key = 'IDACO_CURRENT_SESSION'

RETURN 0

GO
/****** Object:  StoredProcedure [dbo].[sp_Update_Device_NumParam_by_ID]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[sp_Update_Device_NumParam_by_ID]
	-- Add the parameters for the stored procedure here
	@DevID INT,
	@ParamID INT,
	@ParamValue REAL
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	update idaco_devices
	set NumParam = @ParamValue 
	where ID = @ParamID and ParentID = @DevID
END


GO
/****** Object:  StoredProcedure [dbo].[sp_Update_Device_NumParam_by_Name]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[sp_Update_Device_NumParam_by_Name] 
	-- Add the parameters for the stored procedure here
	@DevID INT,
	@ParamName NVARCHAR(MAX),
	@ParamValue REAL
	
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

update idaco_devices
set NumParam = @ParamValue 
where Name = @ParamName and ParentID = @DevID


END


GO
/****** Object:  StoredProcedure [dbo].[sp_Update_Device_StringParam_by_ID]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[sp_Update_Device_StringParam_by_ID]
	-- Add the parameters for the stored procedure here
	@DevID INT,
	@ParamID INT,
	@ParamValue NVARCHAR(MAX)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	update idaco_devices
	set StringParam = @ParamValue  
	where ID = @ParamID and ParentID = @DevID

END


GO
/****** Object:  StoredProcedure [dbo].[sp_Update_Device_StringParam_by_Name]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[sp_Update_Device_StringParam_by_Name]
	-- Add the parameters for the stored procedure here
	@DevID INT,
	@ParamName NVARCHAR(MAX),
	@ParamValue NVARCHAR(MAX)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	update idaco_devices
	set StringParam = @ParamValue 
	where Name = @ParamName and ParentID = @DevID


END


GO
/****** Object:  StoredProcedure [dbo].[sp_Update_IDACO_Device_Status]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[sp_Update_IDACO_Device_Status]
		@device_list_id INT,
		@device_status_param_key INT,
	    @device_status_param_value FLOAT,

		@session_id INT = NULL
AS
	

	IF(NOT EXISTS ( SELECT * FROM dbo.IDACO_device_status WHERE device_list_id = @device_list_id AND device_status_param_key = @device_status_param_key ))
	BEGIN
	INSERT dbo.IDACO_device_status
	        ( device_list_id ,
	          session_id ,
	          device_status_param_key ,
	          device_status_param_value ,
	          device_status_datetime
	        )
	VALUES  ( @device_list_id , -- device_list_id - int
	          @session_id , -- session_id - int
	          @device_status_param_key , -- device_status_param_key - int
	          @device_status_param_value, -- device_status_param_value - float
	          GETDATE()  -- device_status_datetime - datetime
	        )
	END
	ELSE
	BEGIN
		UPDATE dbo.IDACO_device_status
		SET device_status_param_value = @device_status_param_value, device_status_datetime = GETDATE()
		WHERE device_list_id = @device_list_id AND device_status_param_key = @device_status_param_key
	END

RETURN 0

GO
/****** Object:  StoredProcedure [dbo].[sp_Update_IDACO_Device_Status_SCB]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[sp_Update_IDACO_Device_Status_SCB]
		@device_com_bus_id INT,
		@device_com_bus_param_id INT,
	    @device_status_param_value FLOAT,

		@session_id INT = NULL
AS
	

	declare @device_list_id INT
	DECLARE @device_parameter_list_id INT

	SET @device_list_id =( SELECT TOP 1 device_list_id FROM dbo.IDACO_device WHERE device_com_bus_id = @device_com_bus_id)

	SET @device_parameter_list_id = ( SELECT device_parameter_list_id FROM dbo.IDACO_device_parameter_list WHERE device_parameter_id =@device_com_bus_param_id AND device_list_id = @device_list_id )


	IF(NOT EXISTS ( SELECT * FROM dbo.IDACO_device_status WHERE device_list_id = @device_list_id AND device_status_param_key = @device_parameter_list_id ))
	BEGIN
	INSERT dbo.IDACO_device_status
	        ( device_list_id ,
	          session_id ,
	          device_status_param_key ,
	          device_status_param_value ,
	          device_status_datetime
	        )
	VALUES  ( @device_list_id , -- device_list_id - int
	          @session_id , -- session_id - int
	          @device_parameter_list_id , -- device_status_param_key - int
	          @device_status_param_value, -- device_status_param_value - float
	          GETDATE()  -- device_status_datetime - datetime
	        )
	END
	ELSE
	BEGIN
		UPDATE dbo.IDACO_device_status
		SET device_status_param_value = @device_status_param_value, device_status_datetime = GETDATE()
		WHERE device_list_id = @device_list_id AND device_status_param_key = @device_parameter_list_id
	END

RETURN 0

GO
/****** Object:  StoredProcedure [dbo].[sp_Update_IDACO_Device_Status_Target]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[sp_Update_IDACO_Device_Status_Target]
		@device_list_id INT,
		@device_status_param_key INT,
	    @device_status_param_value FLOAT,

		@session_id INT = NULL
AS
	

	IF(NOT EXISTS ( SELECT * FROM dbo.IDACO_device_status_target WHERE device_list_id = @device_list_id AND device_status_param_key = @device_status_param_key ))
	BEGIN
	INSERT dbo.IDACO_device_status_target
	        ( device_list_id ,
	          session_id ,
	          device_status_param_key ,
	          device_status_param_value ,
	          device_status_datetime
	        )
	VALUES  ( @device_list_id , -- device_list_id - int
	          @session_id , -- session_id - int
	          @device_status_param_key , -- device_status_param_key - int
	          @device_status_param_value, -- device_status_param_value - float
	          GETDATE()  -- device_status_datetime - datetime
	        )
	END
	ELSE
	BEGIN
		UPDATE dbo.IDACO_device_status_target
		SET device_status_param_value = @device_status_param_value, device_status_datetime = GETDATE()
		WHERE device_list_id = @device_list_id AND device_status_param_key = @device_status_param_key
	END

RETURN 0

GO
/****** Object:  StoredProcedure [dbo].[sp_Update_IDACO_Device_Status_Target_ByName]    Script Date: 05.05.2017 16:45:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[sp_Update_IDACO_Device_Status_Target_ByName]
		@device_name NVARCHAR(MAX),
		@device_status_param_name NVARCHAR(MAX),
	    @device_status_param_value FLOAT,

		@session_id INT = NULL
AS
	

	DECLARE @device_id INT
	DECLARE @device_param_id INT


	SET @device_id = (SELECT TOP 1 device_list_id FROM dbo.IDACO_device WHERE device_name = @device_name)

	SET @device_param_id = (SELECT TOP 1 device_parameter_id FROM dbo.IDACO_device_parameter_list WHERE device_list_id = @device_id AND device_parameter_name = @device_status_param_name )


	IF (@device_id IS NOT NULL AND @device_param_id IS NOT NULL) 
	BEGIN
		PRINT N'Update ' 
				+ ( CAST(@device_id AS NVARCHAR(MAX))) 
				+ ' at ' + ( CAST(@device_param_id AS NVARCHAR(MAX)))
				+ ' with ' + ( CAST(@device_status_param_value AS NVARCHAR(MAX)))

	EXEC dbo.sp_Update_IDACO_Device_Status_Target 
		@device_list_id = @device_id, -- int
	    @device_status_param_key = @device_param_id, -- int
	    @device_status_param_value = @device_status_param_value, -- float
	    @session_id = @session_id -- int
	RETURN 0
	END
	ELSE
	BEGIN
		PRINT N'Value Update Failed ' 
				--+ ( CAST(@device_id AS NVARCHAR(MAX))) 
				--+ ' at ' + ( CAST(@device_param_id AS NVARCHAR(MAX)))
				--+ ' with ' + ( CAST(@device_status_param_value AS NVARCHAR(MAX)))
	RETURN -1
	END



GO
USE [master]
GO
ALTER DATABASE [IDACO] SET  READ_WRITE 
GO
