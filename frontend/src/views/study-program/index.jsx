/* eslint-disable no-unused-vars */
import React, { useState, useEffect } from "react";
import {
  Card,
  Button,
  Table,
  message,
  Divider,
  Modal,
  Row,
  Col,
} from "antd";
import {
  EditOutlined,
  DeleteOutlined,
  PlusOutlined,
} from "@ant-design/icons";
import {
  getStudyPrograms,
  deleteStudyProgram,
  editStudyProgram,
  addStudyProgram,
} from "@/api/studyProgram";
import TypingCard from "@/components/TypingCard";
import EditStudyProgramForm from "./forms/edit-study-program-form";
import AddStudyProgramForm from "./forms/add-study-program-form";
import { Skeleton } from "antd";

const StudyProgram = () => {
  const [studyPrograms, setStudyPrograms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [addModalVisible, setAddModalVisible] = useState(false);
  const [addModalLoading, setAddModalLoading] = useState(false);
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [editModalLoading, setEditModalLoading] = useState(false);
  const [currentRowData, setCurrentRowData] = useState({});

  const fetchData = async () => {
    setLoading(true);
    try {
      const programsRes = await getStudyPrograms();
      if (programsRes.data.statusCode === 200) {
        setStudyPrograms(programsRes.data.content || []);
      } else {
        message.error("Gagal mengambil data program studi");
      }
    } catch (error) {
      message.error("Terjadi kesalahan: " + error.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleAddStudyProgramOk = async (values) => {
    setAddModalLoading(true);
    try {
      await addStudyProgram(values);
      setAddModalVisible(false);
      message.success("Program Studi berhasil ditambahkan!");
      fetchData();
    } catch (error) {
      message.error("Gagal menambahkan: " + error.message);
    } finally {
      setAddModalLoading(false);
    }
  };

  const handleEditStudyProgramOk = async (values) => {
    setEditModalLoading(true);
    try {
      const { id, ...dataToSend } = values; // Jangan kirim 'id' ke backend di payload body
      await editStudyProgram(dataToSend, currentRowData.id);
      setEditModalVisible(false);
      message.success("Program Studi berhasil diperbarui!");
      fetchData();
    } catch (error) {
      message.error("Gagal memperbarui: " + error.message);
    } finally {
      setEditModalLoading(false);
    }
  };

  const handleDeleteStudyProgram = (row) => {
    Modal.confirm({
      title: "Konfirmasi",
      content: "Apakah Anda yakin ingin menghapus program studi ini?",
      okText: "Ya",
      okType: "danger",
      cancelText: "Tidak",
      onOk: async () => {
        try {
          await deleteStudyProgram({ id: row.id });
          message.success("Berhasil dihapus");
          fetchData();
        } catch (error) {
          message.error("Gagal menghapus: " + error.message);
        }
      },
    });
  };

  const handleCancel = () => {
    setAddModalVisible(false);
    setEditModalVisible(false);
  };

  const columns = [
    {
      title: "No",
      key: "index",
      align: "center",
      render: (_, __, index) => index + 1,
    },
    {
      title: "ID Program Studi",
      dataIndex: "id",
      key: "id",
      align: "center",
    },
    {
      title: "Jurusan",
      key: "department",
      align: "center",
      render: (_, row) => row.department?.name ?? "-",
    },
    {
      title: "Nama Program Studi",
      dataIndex: "name",
      key: "name",
      align: "center",
    },
    {
      title: "Deskripsi",
      dataIndex: "description",
      key: "description",
      align: "center",
    },
    {
      title: "Operasi",
      key: "action",
      align: "center",
      render: (_, row) => (
        <span>
          <Button
            type="primary"
            shape="circle"
            icon={<EditOutlined />}
            onClick={() => {
              setCurrentRowData({ ...row });
              setEditModalVisible(true);
            }}
          />
          <Divider type="vertical" />
          <Button
            type="primary"
            danger
            shape="circle"
            icon={<DeleteOutlined />}
            onClick={() => handleDeleteStudyProgram(row)}
          />
        </span>
      ),
    },
  ];

  const cardContent =
    "Di sini, Anda dapat mengelola program studi di sistem, seperti menambahkan program studi baru, atau mengubah program studi yang sudah ada di sistem.";

  return (
    <div className="app-container">
      <TypingCard title="Manajemen Program Studi" source={cardContent} />
      <br />
      {loading ? (
        <Card>
          <Skeleton active paragraph={{ rows: 8 }} />
        </Card>
      ) : (
        <Card style={{ overflowX: "scroll" }}>
          <Row justify="start" style={{ marginBottom: 16 }}>
            <Col>
              <Button
                type="primary"
                icon={<PlusOutlined />}
                onClick={() => setAddModalVisible(true)}
              >
                Tambahkan Program Studi
              </Button>
            </Col>
          </Row>

          <Table
            rowKey="id"
            dataSource={studyPrograms}
            columns={columns}
            pagination={{ pageSize: 10 }}
          />

          <AddStudyProgramForm
            visible={addModalVisible}
            confirmLoading={addModalLoading}
            onCancel={handleCancel}
            onOk={handleAddStudyProgramOk}
          />

          <EditStudyProgramForm
            currentRowData={currentRowData}
            visible={editModalVisible}
            confirmLoading={editModalLoading}
            onCancel={handleCancel}
            onOk={handleEditStudyProgramOk}
          />
        </Card>
      )}
    </div>
  );
};

export default StudyProgram;
