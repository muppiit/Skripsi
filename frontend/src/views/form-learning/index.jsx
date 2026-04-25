/* eslint-disable no-unused-vars */
import React, { useState, useEffect, useRef } from "react";
import { Card, Button, Table, message, Divider } from "antd";
import {
  getFormLearnings,
  deleteFormLearning,
  editFormLearning,
  addFormLearning,
} from "@/api/formLearning";
import TypingCard from "@/components/TypingCard";
import EditFormLearningForm from "./forms/edit-formLearning-form";
import AddFormLearningForm from "./forms/add-formLearning-form";

const { Column } = Table;

const FormLearning = () => {
  const [formLearnings, setFormLearnings] = useState([]);
  const [editFormLearningModalVisible, setEditFormLearningModalVisible] =
    useState(false);
  const [editFormLearningModalLoading, setEditFormLearningModalLoading] =
    useState(false);
  const [currentRowData, setCurrentRowData] = useState({});
  const [addFormLearningModalVisible, setAddFormLearningModalVisible] =
    useState(false);
  const [addFormLearningModalLoading, setAddFormLearningModalLoading] =
    useState(false);

  const editFormLearningFormRef = useRef();
  const addFormLearningFormRef = useRef();

  const fetchFormLearnings = async () => {
    try {
      const result = await getFormLearnings();
      const { content, statusCode } = result.data;

      if (statusCode === 200) {
        setFormLearnings(content);
      } else {
        message.error("Gagal mengambil data");
      }
    } catch (error) {
      message.error("Terjadi kesalahan: " + error.message);
    }
  };

  const handleEditFormLearning = (row) => {
    setCurrentRowData({ ...row });
    setEditFormLearningModalVisible(true);
  };

  const handleDeleteFormLearning = async (row) => {
    const { id } = row;
    if (id === "admin") {
      message.error("Tidak dapat menghapus Admin!");
      return;
    }

    try {
      await deleteFormLearning({ id });
      message.success("Berhasil dihapus");
      fetchFormLearnings();
    } catch (error) {
      message.error("Gagal menghapus: " + error.message);
    }
  };

  const handleEditFormLearningOk = async (values) => {
    try {
      setEditFormLearningModalLoading(true);
      const { id, ...dataToSend } = values; // Jangan kirim 'id' ke payload backend
      await editFormLearning(dataToSend, currentRowData.id);
      setEditFormLearningModalVisible(false);
      message.success("Berhasil diubah!");
      fetchFormLearnings();
    } catch (error) {
      console.error(error);
      message.error("Gagal mengubah: " + (error.response?.data?.message || error.message));
    } finally {
      setEditFormLearningModalLoading(false);
    }
  };

  const handleCancel = () => {
    setEditFormLearningModalVisible(false);
    setAddFormLearningModalVisible(false);
  };

  const handleAddFormLearning = () => {
    setAddFormLearningModalVisible(true);
  };

  const handleAddFormLearningOk = async (values) => {
    try {
      setAddFormLearningModalLoading(true);
      await addFormLearning(values);
      setAddFormLearningModalVisible(false);
      message.success("Berhasil ditambahkan!");
      fetchFormLearnings();
    } catch (error) {
      console.error(error);
      message.error("Gagal menambahkan: " + (error.response?.data?.message || error.message));
    } finally {
      setAddFormLearningModalLoading(false);
    }
  };

  useEffect(() => {
    fetchFormLearnings();
  }, []);

  const title = (
    <span>
      <Button type="primary" onClick={handleAddFormLearning}>
        Tambahkan bentuk pembelajaran
      </Button>
    </span>
  );

  const cardContent = `Di sini, Anda dapat mengelola bentuk pembelajaran di sistem, seperti menambahkan bentuk pembelajaran baru, atau mengubah bentuk pembelajaran yang sudah ada di sistem.`;

  return (
    <div className="app-container">
      <TypingCard title="Manajemen Bentuk Pembelajaran" source={cardContent} />
      <br />
      <Card title={title}>
        <Table
          variant
          rowKey="id"
          dataSource={formLearnings}
          pagination={false}
        >
          <Column
            title="ID Bentuk Pembelajaran"
            dataIndex="id"
            key="id"
            align="center"
          />
          <Column title="Nama" dataIndex="name" key="name" align="center" />
          <Column
            title="Deskripsi Bentuk Pembelajaran"
            dataIndex="description"
            key="description"
            align="center"
          />
          <Column
            title="Operasi"
            key="action"
            width={195}
            align="center"
            render={(text, row) => (
              <span>
                <Button
                  type="primary"
                  shape="circle"
                  icon="edit"
                  title="mengedit"
                  onClick={() => handleEditFormLearning(row)}
                />
                <Divider type="vertical" />
                <Button
                  type="primary"
                  shape="circle"
                  icon="delete"
                  title="menghapus"
                  onClick={() => handleDeleteFormLearning(row)}
                />
              </span>
            )}
          />
        </Table>
      </Card>

      <EditFormLearningForm
        currentRowData={currentRowData}
        wrappedComponentRef={editFormLearningFormRef}
        visible={editFormLearningModalVisible}
        confirmLoading={editFormLearningModalLoading}
        onCancel={handleCancel}
        onOk={handleEditFormLearningOk}
      />

      <AddFormLearningForm
        wrappedComponentRef={addFormLearningFormRef}
        visible={addFormLearningModalVisible}
        confirmLoading={addFormLearningModalLoading}
        onCancel={handleCancel}
        onOk={handleAddFormLearningOk}
      />
    </div>
  );
};

export default FormLearning;
