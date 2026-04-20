/* eslint-disable no-unused-vars */
import React, { useState, useEffect, useRef } from "react";
import {
  Card,
  Button,
  Table,
  message,
  Divider,
  Modal,
  Upload,
  Row,
  Col,
  Spin,
} from "antd";
import { UploadOutlined } from "@ant-design/icons";
import { getRPS, deleteRPS, editRPS, addRPS, importRPS } from "@/api/rps";
import { importRPSDetail } from "@/api/rpsDetail";
import { getSubjects } from "@/api/subject";
import { getStudyPrograms } from "@/api/studyProgram";
import { getLectures } from "@/api/lecture";
import { Link } from "react-router-dom";
import {
  getLearningMediasSoftware,
  getLearningMediasHardware,
} from "@/api/learningMedia";
import * as XLSX from "xlsx";
import TypingCard from "@/components/TypingCard";
import EditRPSForm from "./forms/edit-rps-form";
import AddRPSForm from "./forms/add-rps-form";

const { Column } = Table;

const RPS = () => {
  const [rps, setRps] = useState([]);
  const [learningMediaSoftwares, setLearningMediaSoftwares] = useState([]);
  const [learningMediaHardwares, setLearningMediaHardwares] = useState([]);
  const [subjects, setSubjects] = useState([]);
  const [studyPrograms, setStudyPrograms] = useState([]);
  const [lectures, setLectures] = useState([]);
  const [importModalVisible, setImportModalVisible] = useState(false);
  const [importFile, setImportFile] = useState(null);
  const [uploading, setUploading] = useState(false);
  const [modalState, setModalState] = useState({
    editVisible: false,
    editLoading: false,
    addVisible: false,
    addLoading: false,
  });
  const [currentRowData, setCurrentRowData] = useState({});

  const editFormRef = useRef();
  const addFormRef = useRef();

  const fetchData = async () => {
    try {
      const rpsResult = await getRPS();
      if (rpsResult.data.statusCode === 200) {
        setRps(rpsResult.data.content);
      }

      const softwareResult = await getLearningMediasSoftware();
      if (softwareResult.data.statusCode === 200) {
        setLearningMediaSoftwares(softwareResult.data.content);
      }

      const hardwareResult = await getLearningMediasHardware();
      if (hardwareResult.data.statusCode === 200) {
        setLearningMediaHardwares(hardwareResult.data.content);
      }

      const subjectsResult = await getSubjects();
      if (subjectsResult.data.statusCode === 200) {
        setSubjects(subjectsResult.data.content);
      }

      const studyProgramResult = await getStudyPrograms();
      if (studyProgramResult.data.statusCode === 200) {
        setStudyPrograms(studyProgramResult.data.content);
      }

      const lecturesResult = await getLectures();
      if (lecturesResult.data.statusCode === 200) {
        setLectures(lecturesResult.data.content);
      }
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleEditRPS = (row) => {
    setCurrentRowData({ ...row });
    setModalState((prev) => ({ ...prev, editVisible: true }));
  };

  const handleDeleteRPS = async (row) => {
    if (row.id === "admin") {
      message.error("Berhasil Dibuat");
      return;
    }

    try {
      await deleteRPS({ id: row.id });
      message.success("berhasil dihapus");
      fetchData();
    } catch (error) {
      message.error("Gagal menghapus");
    }
  };

  const handleEditRPSOk = () => {
    const form = editFormRef.current?.props.form;
    form?.validateFields(async (err, values) => {
      if (err) return;

      setModalState((prev) => ({ ...prev, editLoading: true }));
      try {
        await editRPS(values);
        form.resetFields();
        setModalState((prev) => ({
          ...prev,
          editVisible: false,
          editLoading: false,
        }));
        message.success("Berhasil!");
        fetchData();
      } catch (error) {
        message.error("Gagal mengubah");
        setModalState((prev) => ({ ...prev, editLoading: false }));
      }
    });
  };

  const handleCancel = () => {
    setModalState((prev) => ({
      ...prev,
      editVisible: false,
      addVisible: false,
    }));
  };

  const handleAddRPS = () => {
    setModalState((prev) => ({ ...prev, addVisible: true }));
  };

  const handleAddRPSOk = () => {
    const form = addFormRef.current?.props.form;
    form?.validateFields(async (err, values) => {
      if (err) return;

      setModalState((prev) => ({ ...prev, addLoading: true }));
      try {
        await addRPS(values);
        form.resetFields();
        setModalState((prev) => ({
          ...prev,
          addVisible: false,
          addLoading: false,
        }));
        message.success("Berhasil!");
        fetchData();
      } catch (error) {
        message.error("Gagal menambahkan, coba lagi!");
        setModalState((prev) => ({ ...prev, addLoading: false }));
      }
    });
  };

  const handleImportFile = (file) => {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = async (e) => {
        try {
          const data = new Uint8Array(e.target.result);
          const workbook = XLSX.read(data, { type: "array" });

          let successCount = 0;
          let errorCount = 0;
          let errorMessages = [];

          // Import RPS dari sheet pertama
          const rpsSheetName = workbook.SheetNames[0];
          if (rpsSheetName) {
            const rpsWorksheet = workbook.Sheets[rpsSheetName];
            const rpsData = XLSX.utils.sheet_to_json(rpsWorksheet);

            if (rpsData.length > 0) {
              try {
                const rpsResponse = await importRPS(file);
                if (rpsResponse.status === 200) {
                  successCount += rpsData.length;
                  message.success(`Import RPS berhasil: ${rpsData.length} data`);
                } else {
                  errorCount += rpsData.length;
                  errorMessages.push("Gagal import RPS");
                }
              } catch (error) {
                errorCount += rpsData.length;
                errorMessages.push(`Gagal import RPS: ${error.message}`);
              }
            }
          }

          // Import RPS Detail dari sheet kedua jika ada
          if (workbook.SheetNames.length > 1) {
            const detailSheetName = workbook.SheetNames[1];
            const detailWorksheet = workbook.Sheets[detailSheetName];
            const detailData = XLSX.utils.sheet_to_json(detailWorksheet);

            if (detailData.length > 0) {
              try {
                const detailResponse = await importRPSDetail(file);
                if (detailResponse.status === 200) {
                  successCount += detailData.length;
                  message.success(
                    `Import RPS Detail berhasil: ${detailData.length} data`
                  );
                } else {
                  errorCount += detailData.length;
                  errorMessages.push("Gagal import RPS Detail");
                }
              } catch (error) {
                errorCount += detailData.length;
                errorMessages.push(
                  `Gagal import RPS Detail: ${error.message}`
                );
              }
            }
          }

          // Tampilkan hasil akhir
          if (successCount > 0) {
            message.success(
              `✅ Import berhasil! ${successCount} data berhasil ditambahkan.`
            );
          }

          if (errorCount > 0) {
            const displayErrors = errorMessages.slice(0, 3);
            const moreErrors =
              errorMessages.length > 3
                ? `\n... dan ${errorMessages.length - 3} error lainnya`
                : "";

            message.warning({
              content: `⚠️ ${errorCount} data gagal: ${displayErrors.join(
                "\n"
              )}${moreErrors}`,
            });
          }

          setImportModalVisible(false);
          setImportFile(null);
          fetchData();
          resolve();
        } catch (error) {
          console.error("Error during file upload:", error);
          message.error("❌ Gagal mengupload file: " + error.message);
          reject(error);
        }
      };

      reader.readAsArrayBuffer(file);
    });
  };

  const cardContent = `Di sini, Anda dapat mengelola RPS sesuai dengan mata kuliah yang diampu. Di bawah ini dapat menampilkan list RPS yang ada.`;

  const handleImportCancel = () => {
    setImportModalVisible(false);
    setImportFile(null);
  };

  const handleImportOk = async () => {
    if (!importFile) {
      message.error("Silakan pilih file terlebih dahulu");
      return;
    }

    setUploading(true);
    try {
      await handleImportFile(importFile);
    } finally {
      setUploading(false);
    }
  };

  return (
    <div className="app-container">
      <TypingCard title="Manajemen RPS" source={cardContent} />
      <br />
      <Row gutter={[16, 16]} justify="start">
        <Col>
          <Button type="primary" onClick={handleAddRPS}>
            + Tambahkan RPS
          </Button>
        </Col>
        <Col>
          <Button
            icon={<UploadOutlined />}
            onClick={() => setImportModalVisible(true)}
          >
            Import RPS
          </Button>
        </Col>
      </Row>
      <br />
      <Card title="Daftar RPS">
        <Table variant rowKey="id" dataSource={rps} pagination={false}>
          <Column title="ID RPS" dataIndex="id" key="id" align="center" />
          <Column title="Nama" dataIndex="name" key="name" align="center" />
          <Column
            title="Semester"
            dataIndex="semester"
            key="semester"
            align="center"
          />
          <Column title="SKS" dataIndex="sks" key="sks" align="center" />
          <Column
            title="Mata Kuliah"
            dataIndex="subject.name"
            key="subject.name"
            align="center"
          />
          <Column
            title="Dosen Pengembang"
            dataIndex="dev_lecturers"
            key="dev_lecturers"
            align="center"
            render={(dev_lecturers) =>
              dev_lecturers?.map((l) => l.name).join(", ") || ""
            }
          />
          <Column
            title="Operasi"
            key="action"
            width={195}
            align="center"
            render={(_, row) => (
              <span>
                <Button
                  type="primary"
                  shape="circle"
                  icon="edit"
                  title="mengedit"
                  onClick={() => handleEditRPS(row)}
                />
                <Divider type="vertical" />
                <Link to={`/rps/${row.id}`}>
                  <Button
                    type="primary"
                    shape="circle"
                    icon="diff"
                    title="menghapus"
                  />
                </Link>
                <Divider type="vertical" />
                <Button
                  type="primary"
                  shape="circle"
                  icon="delete"
                  title="menghapus"
                  onClick={() => handleDeleteRPS(row)}
                />
              </span>
            )}
          />
        </Table>
      </Card>

      <EditRPSForm
        wrappedComponentRef={editFormRef}
        currentRowData={currentRowData}
        visible={modalState.editVisible}
        confirmLoading={modalState.editLoading}
        onCancel={handleCancel}
        onOk={handleEditRPSOk}
        learningMediaSoftwares={learningMediaSoftwares}
        learningMediaHardwares={learningMediaHardwares}
        studyPrograms={studyPrograms}
        subjects={subjects}
        lectures={lectures}
      />

      <AddRPSForm
        wrappedComponentRef={addFormRef}
        visible={modalState.addVisible}
        confirmLoading={modalState.addLoading}
        onCancel={handleCancel}
        onOk={handleAddRPSOk}
        learningMediaSoftwares={learningMediaSoftwares}
        learningMediaHardwares={learningMediaHardwares}
        studyPrograms={studyPrograms}
        subjects={subjects}
        lectures={lectures}
      />

      <Modal
        title="Import RPS"
        visible={importModalVisible}
        onOk={handleImportOk}
        onCancel={handleImportCancel}
        confirmLoading={uploading}
        width={600}
      >
        <div style={{ marginBottom: "20px" }}>
          <h4>📋 Instruksi Import:</h4>
          <p>
            1. Download template file RPS di bawah ini<br />
            2. Isi data sesuai format yang disediakan<br />
            3. Upload file dengan format .csv atau .xlsx<br />
            4. Sistem akan memvalidasi dan menyimpan data
          </p>
          <div>
            <strong>Template Download:</strong>
            <br />
            <a href="/templates/import-template-rps.csv" download>
              📥 Download Template RPS (CSV)
            </a>
            {" | "}
            <a href="/templates/import-template-rps-detail.csv" download>
              📥 Download Template RPS Detail (CSV)
            </a>
          </div>
        </div>

        <Divider />

        <div>
          <Spin spinning={uploading}>
            <Upload
              accept=".csv,.xlsx,.xls"
              maxCount={1}
              beforeUpload={(file) => {
                setImportFile(file);
                return false;
              }}
              onRemove={() => setImportFile(null)}
            >
              <Button icon={<UploadOutlined />}>
                Pilih File RPS
              </Button>
            </Upload>
            {importFile && (
              <p style={{ marginTop: "10px", color: "#1890ff" }}>
                ✅ File dipilih: {importFile.name}
              </p>
            )}
          </Spin>
        </div>

        <Divider />

        <div style={{ fontSize: "12px", color: "#999" }}>
          <strong>Format File:</strong> CSV atau Excel (.xlsx, .xls)
          <br />
          <strong>Kolom RPS:</strong> id, name, sks, semester, cpl_prodi,
          cpl_mk, subject_id, study_program_id
          <br />
          <strong>Kolom RPS Detail:</strong> rps_id, rps_name, sub_cp_mk,
          weight, learning_materials
        </div>
      </Modal>
    </div>
  );
};

export default RPS;
