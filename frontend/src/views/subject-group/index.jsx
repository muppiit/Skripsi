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
  getSubjectGroups,
  deleteSubjectGroup,
  editSubjectGroup,
  addSubjectGroup,
} from "@/api/subjectGroup";
import TypingCard from "@/components/TypingCard";
import EditSubjectGroupForm from "./forms/edit-subject-group-form";
import AddSubjectGroupForm from "./forms/add-subject-group-form";
import { Skeleton } from "antd";

const SubjectGroup = () => {
  const [subjectGroups, setSubjectGroups] = useState([]);
  const [loading, setLoading] = useState(true);
  const [addModalVisible, setAddModalVisible] = useState(false);
  const [addModalLoading, setAddModalLoading] = useState(false);
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [editModalLoading, setEditModalLoading] = useState(false);
  const [currentRowData, setCurrentRowData] = useState({});

  const fetchSubjectGroups = async () => {
    setLoading(true);
    try {
      const result = await getSubjectGroups();
      if (result.data.statusCode === 200) {
        setSubjectGroups(result.data.content || []);
      } else {
        message.error("Gagal mengambil data rumpun mata kuliah");
      }
    } catch (error) {
      message.error("Terjadi kesalahan: " + error.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSubjectGroups();
  }, []);

  const handleAddSubjectGroupOk = async (values) => {
    setAddModalLoading(true);
    try {
      await addSubjectGroup(values);
      setAddModalVisible(false);
      message.success("Rumpun Mata Kuliah berhasil ditambahkan!");
      fetchSubjectGroups();
    } catch (error) {
      message.error("Gagal menambahkan: " + error.message);
    } finally {
      setAddModalLoading(false);
    }
  };

  const handleEditSubjectGroupOk = async (values) => {
    setEditModalLoading(true);
    try {
      // Hapus 'id' dari payload karena backend akan menolaknya dengan 400 Bad Request
      const { id, ...dataToSend } = values; 
      await editSubjectGroup(dataToSend, currentRowData.id);
      setEditModalVisible(false);
      message.success("Rumpun Mata Kuliah berhasil diperbarui!");
      fetchSubjectGroups();
    } catch (error) {
      message.error("Gagal memperbarui: " + error.message);
    } finally {
      setEditModalLoading(false);
    }
  };

  const handleDeleteSubjectGroup = (row) => {
    Modal.confirm({
      title: "Konfirmasi",
      content: "Apakah Anda yakin ingin menghapus rumpun mata kuliah ini?",
      okText: "Ya",
      okType: "danger",
      cancelText: "Tidak",
      onOk: async () => {
        try {
          await deleteSubjectGroup({ id: row.id });
          message.success("Berhasil dihapus");
          fetchSubjectGroups();
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
      title: "ID Rumpun Mata Kuliah",
      dataIndex: "id",
      key: "id",
      align: "center",
    },
    {
      title: "Nama Rumpun",
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
            onClick={() => handleDeleteSubjectGroup(row)}
          />
        </span>
      ),
    },
  ];

  const cardContent =
    "Di sini, Anda dapat mengelola rumpun mata kuliah di sistem, seperti menambahkan rumpun mata kuliah baru, atau mengubah rumpun mata kuliah yang sudah ada di sistem.";

  return (
    <div className="app-container">
      <TypingCard title="Manajemen Rumpun Mata Kuliah" source={cardContent} />
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
                Tambahkan Rumpun Mata Kuliah
              </Button>
            </Col>
          </Row>

          <Table
            rowKey="id"
            dataSource={subjectGroups}
            columns={columns}
            pagination={{ pageSize: 10 }}
          />

          <AddSubjectGroupForm
            visible={addModalVisible}
            confirmLoading={addModalLoading}
            onCancel={handleCancel}
            onOk={handleAddSubjectGroupOk}
          />

          <EditSubjectGroupForm
            currentRowData={currentRowData}
            visible={editModalVisible}
            confirmLoading={editModalLoading}
            onCancel={handleCancel}
            onOk={handleEditSubjectGroupOk}
          />
        </Card>
      )}
    </div>
  );
};

export default SubjectGroup;
